package org.sopt36.ninedotserver.mandalart.usecase.command;

import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_COMPLETED;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_CONFLICT;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_LIMITED;
import static org.sopt36.ninedotserver.mandalart.exception.CoreGoalErrorCode.CORE_GOAL_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;
import static org.sopt36.ninedotserver.mandalart.exception.SubGoalErrorCode.SUB_GOAL_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.model.CoreGoal;
import org.sopt36.ninedotserver.mandalart.model.CoreGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.model.Cycle;
import org.sopt36.ninedotserver.mandalart.model.Mandalart;
import org.sopt36.ninedotserver.mandalart.model.SubGoal;
import org.sopt36.ninedotserver.mandalart.model.SubGoalSnapshot;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalAiCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalCreateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.CoreGoalUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.MandalartUpdateRequest;
import org.sopt36.ninedotserver.mandalart.dto.request.SubGoalUpdateWithPositionRequest;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalAiDetailResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalAiListResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalCreateResponse;
import org.sopt36.ninedotserver.mandalart.exception.CoreGoalException;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.exception.SubGoalException;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.CoreGoalSnapshotRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.MandalartRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalRepositoryPort;
import org.sopt36.ninedotserver.mandalart.port.out.SubGoalSnapshotRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CoreGoalCommandService {

    private static final int MAX_MANDALART = 8;
    private static final boolean AI_GENERATABLE = true;

    private final CoreGoalRepositoryPort coreGoalRepository;
    private final MandalartRepositoryPort mandalartRepository;
    private final CoreGoalSnapshotRepositoryPort coreGoalSnapshotRepository;
    private final SubGoalRepositoryPort subGoalRepository;
    private final SubGoalSnapshotRepositoryPort subGoalSnapshotRepository;

    @Transactional
    public CoreGoalCreateResponse createCoreGoal(
        Long userId,
        Long mandalartId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        validateCanCreateCoreGoal(mandalart, userId, coreGoalCreateRequest);

        CoreGoal coreGoal = CoreGoal.create(
            mandalart,
            coreGoalCreateRequest.position(),
            AI_GENERATABLE
        );
        coreGoalRepository.save(coreGoal);

        CoreGoalSnapshot coreGoalSnapshot = CoreGoalSnapshot.create(
            coreGoal,
            coreGoalCreateRequest.title(),
            LocalDateTime.now(),
            null
        );
        coreGoalSnapshotRepository.save(coreGoalSnapshot);

        return CoreGoalCreateResponse.from(coreGoalSnapshot);
    }

    @Transactional
    public void updateCoreGoal(
        Long userId,
        Long coreGoalSnapshotId,
        CoreGoalUpdateRequest coreGoalUpdateRequest
    ) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);

        coreGoalSnapshot.updateTitle(coreGoalUpdateRequest.title());

        coreGoalSnapshotRepository.save(coreGoalSnapshot);
    }

    @Transactional
    public void deleteCoreGoal(Long userId, Long coreGoalSnapshotId) {
        CoreGoalSnapshot coreGoalSnapshot = getExistingCoreGoal(coreGoalSnapshotId);
        coreGoalSnapshot.verifyCoreGoalUser(userId);

        CoreGoal coreGoal = coreGoalSnapshot.getCoreGoal();

        subGoalSnapshotRepository.deleteBySubGoal_CoreGoal(coreGoal);
        subGoalRepository.deleteByCoreGoal(coreGoal);

        coreGoalSnapshotRepository.delete(coreGoalSnapshot);
        coreGoalRepository.delete(coreGoal);
    }

    @Transactional
    public CoreGoalAiListResponse createAiCoreGoals(
        Long userId,
        Long mandalartId,
        CoreGoalAiCreateRequest createRequest
    ) {
        // TODO ValidateCanCreate 어떻게 쓸지 생각해서 나중에 얘로 바꾸기
        Mandalart mandalart = getExistingAndValidate(mandalartId, userId);
        validateCoreGoalCounts(mandalartId, createRequest);

        List<Integer> freePositions = findFreePositions(mandalartId, createRequest.goals().size());
        List<CoreGoal> coreGoals = buildCoreGoals(mandalart, freePositions);
        List<CoreGoalSnapshot> snapshots = buildSnapshots(coreGoals, createRequest.goals());

        coreGoalRepository.saveAll(coreGoals);
        coreGoalSnapshotRepository.saveAll(snapshots);

        return CoreGoalAiListResponse.of(toDetailResponses(coreGoals, snapshots));
    }

    @Transactional
    public void updateMandalart(
        Long userId,
        Long mandalartId,
        MandalartUpdateRequest updateRequest
    ) {
        Mandalart mandalart = getExistingAndValidate(mandalartId, userId);

        int position = updateRequest.coreGoal().position();
        CoreGoal coreGoal = coreGoalRepository.findByMandalartIdAndPosition(mandalartId, position)
            .orElseGet(() -> {
                CoreGoal newGoal = CoreGoal.create(mandalart, position, false);
                coreGoalRepository.save(newGoal);
                return newGoal;
            });

        Optional<CoreGoalSnapshot> optionalLatest = coreGoalSnapshotRepository
            .findTopByCoreGoal_IdOrderByCreatedAtDesc(coreGoal.getId());
        if (optionalLatest.isEmpty()) {
            CoreGoalSnapshot initialSnapshot = CoreGoalSnapshot.create(
                coreGoal,
                updateRequest.coreGoal().title(),
                LocalDateTime.now(),
                null
            );
            coreGoalSnapshotRepository.save(initialSnapshot);
            updateSubgoals(updateRequest, coreGoal);
            return;
        }

        CoreGoalSnapshot latestSnapshot = optionalLatest.get();
        boolean isCreatedToday = latestSnapshot.getValidFrom().toLocalDate()
            .equals(LocalDate.now());
        if (isCreatedToday) {
            latestSnapshot.updateTitle(updateRequest.coreGoal().title());
            coreGoalSnapshotRepository.save(latestSnapshot);
            updateSubgoals(updateRequest, coreGoal);
            return;
        }

        latestSnapshot.updateValidTo(LocalDateTime.now());
        CoreGoalSnapshot newSnapshot = CoreGoalSnapshot.create(
            coreGoal,
            updateRequest.coreGoal().title(),
            LocalDateTime.now(),
            null
        );
        coreGoalSnapshotRepository.saveAll(List.of(latestSnapshot, newSnapshot));
        updateSubgoals(updateRequest, coreGoal);
    }

    private void validateCoreGoalCounts(Long mandalartId, CoreGoalAiCreateRequest createRequest) {
        int existingCoreGoals = coreGoalRepository.countCoreGoalByMandalartId(mandalartId);
        int requestCoreGoals = createRequest.goals().size();
        if (existingCoreGoals + requestCoreGoals > MAX_MANDALART) {
            throw new CoreGoalException(CORE_GOAL_LIMITED);
        }
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

    private void validateCanCreateCoreGoal(
        Mandalart mandalart,
        Long userId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        mandalart.ensureOwnedBy(userId);
        validateCoreGoalLimitNotExceeded(mandalart.getId());
        validateAlreadyExists(mandalart.getId(), coreGoalCreateRequest);
    }

    private void validateCoreGoalLimitNotExceeded(Long mandalartId) {
        if (coreGoalRepository.countCoreGoalByMandalartId(mandalartId) == MAX_MANDALART) {
            throw new CoreGoalException(CORE_GOAL_COMPLETED);
        }
    }

    private void validateAlreadyExists(Long mandalartId,
        CoreGoalCreateRequest coreGoalCreateRequest
    ) {
        if (coreGoalRepository.existsByMandalartIdAndPosition(mandalartId,
            coreGoalCreateRequest.position())) {
            throw new CoreGoalException(CORE_GOAL_CONFLICT);
        }
    }

    private CoreGoalSnapshot getExistingCoreGoal(Long coreGoalId) {
        return coreGoalSnapshotRepository.findById(coreGoalId)
            .orElseThrow(() -> new CoreGoalException(CORE_GOAL_NOT_FOUND));
    }

    private Mandalart getExistingAndValidate(Long mandalartId, Long userId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);
        return mandalart;
    }

    private List<Integer> findFreePositions(Long mandalartId, int requiredCount) {
        List<Integer> occupied = coreGoalRepository.findByMandalartId(mandalartId)
            .stream().map(CoreGoal::getPosition)
            .toList();

        return IntStream.rangeClosed(1, MAX_MANDALART)
            .filter(pos -> !occupied.contains(pos))
            .limit(requiredCount)
            .boxed()
            .toList();
    }

    private List<CoreGoal> buildCoreGoals(Mandalart mandalart, List<Integer> positions) {
        return positions.stream()
            .map(position -> CoreGoal.create(mandalart, position, AI_GENERATABLE))
            .toList();
    }

    private List<CoreGoalSnapshot> buildSnapshots(List<CoreGoal> coreGoals, List<String> titles) {
        LocalDateTime now = LocalDateTime.now();
        return IntStream.range(0, coreGoals.size())
            .mapToObj(i -> CoreGoalSnapshot.create(
                coreGoals.get(i),
                titles.get(i),
                now,
                null))
            .toList();
    }

    private List<CoreGoalAiDetailResponse> toDetailResponses(
        List<CoreGoal> coreGoals,
        List<CoreGoalSnapshot> snapshots
    ) {
        return IntStream.range(0, coreGoals.size())
            .mapToObj(i -> CoreGoalAiDetailResponse.of(coreGoals.get(i), snapshots.get(i)))
            .toList();
    }

    private void updateSubgoals(MandalartUpdateRequest updateRequest, CoreGoal coreGoal) {
        for (SubGoalUpdateWithPositionRequest request : updateRequest.subGoals()) {
            subGoalRepository.findByCoreGoalIdAndPosition(coreGoal.getId(), request.position())
                .ifPresentOrElse(
                    subGoal -> updateExistingSubGoalSnapshot(subGoal, request),
                    () -> createSubGoalWithSnapshot(coreGoal, request)
                );
        }
    }

    private void createSubGoalWithSnapshot(CoreGoal coreGoal,
        SubGoalUpdateWithPositionRequest req) {
        SubGoal subGoal = SubGoal.create(coreGoal, req.position());
        subGoalRepository.save(subGoal);
        SubGoalSnapshot snapshot = SubGoalSnapshot.create(
            subGoal,
            req.title(),
            req.cycle(),
            LocalDateTime.now(),
            null
        );
        subGoalSnapshotRepository.save(snapshot);
    }

    private void updateExistingSubGoalSnapshot(SubGoal subGoal,
        SubGoalUpdateWithPositionRequest request) {
        SubGoalSnapshot latest = subGoalSnapshotRepository
            .findTopBySubGoal_IdOrderByCreatedAtDesc(subGoal.getId())
            .orElseThrow(() -> new SubGoalException(SUB_GOAL_NOT_FOUND));
        handleSubGoalSnapshot(latest, request.title(), request.cycle());
    }

    private void handleSubGoalSnapshot(SubGoalSnapshot latest, String title, Cycle cycle) {
        LocalDate today = LocalDate.now();
        if (latest.getValidFrom().toLocalDate().equals(today)) {
            latest.update(title, cycle);
            subGoalSnapshotRepository.save(latest);
            return;
        }
        latest.updateValidTo(LocalDateTime.now());
        SubGoalSnapshot newSnapshot = SubGoalSnapshot.create(
            latest.getSubGoal(),
            title,
            cycle,
            LocalDateTime.now(),
            null
        );
        subGoalSnapshotRepository.saveAll(List.of(latest, newSnapshot));
    }
}
