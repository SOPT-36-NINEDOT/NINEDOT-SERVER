package org.sopt36.ninedotserver.mandalart.service.query;

import static org.sopt36.ninedotserver.mandalart.exception.MandalartErrorCode.MANDALART_NOT_FOUND;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt36.ninedotserver.mandalart.domain.Mandalart;
import org.sopt36.ninedotserver.mandalart.dto.response.MandalartHistoryResponse;
import org.sopt36.ninedotserver.mandalart.exception.MandalartException;
import org.sopt36.ninedotserver.mandalart.repository.CoreGoalRepository;
import org.sopt36.ninedotserver.mandalart.repository.MandalartRepository;
import org.sopt36.ninedotserver.mandalart.repository.SubGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MandalartQueryService {

    private final MandalartRepository mandalartRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final SubGoalRepository subGoalRepository;

    public MandalartHistoryResponse getMandalartHistory(Long userId, Long mandalartId) {
        Mandalart mandalart = getExistingMandalart(mandalartId);
        mandalart.ensureOwnedBy(userId);

        return MandalartHistoryResponse.of(mandalart, LocalDate.now());
    }

    private Mandalart getExistingMandalart(Long mandalartId) {
        return mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MANDALART_NOT_FOUND));
    }

}
