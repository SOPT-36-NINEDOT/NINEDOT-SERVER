package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalIdResponse;
import org.sopt36.ninedotserver.mandalart.dto.response.CoreGoalIdsResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CoreGoalQueryService {

    private final CoreGoalRepository coreGoalRepository;
    private final MandalartRepository mandalartRepository;

    public CoreGoalIdsResponse getCoreGoalIds(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        List<CoreGoalIdResponse> ids = findCoreGoalIds(mandalartId);

        return CoreGoalIdsResponse.of(ids);
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

    private List<CoreGoalIdResponse> findCoreGoalIds(Long mandalartId) {
        return coreGoalRepository
            .findAllByMandalartIdOrderByPosition(mandalartId)
            .stream()
            .map(CoreGoalIdResponse::from)
            .toList();
    }

}
