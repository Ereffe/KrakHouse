package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnTransaction;
import pk.backend.infrastructure.dto.rcn.RcnTransactionDto;
import pk.backend.infrastructure.repository.RcnTransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionUpsertService {

    private final RcnTransactionRepository repository;

    @Transactional
    public RcnTransaction upsert(RcnTransactionDto dto) {
        RcnTransaction transaction = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnTransaction(dto.gmlId()));

        transaction.setTransactionCode(dto.transactionCode());
        transaction.setPrice(dto.grossPrice());
        transaction.setPropertyRef(dto.propertyRef());

        return repository.save(transaction);
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnTransaction gmlId is required");
        }
        return gmlId;
    }
}
