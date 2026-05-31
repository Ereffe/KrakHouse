package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnLocal;
import pk.backend.infrastructure.dto.rcn.RcnLocalDto;
import pk.backend.infrastructure.repository.RcnLocalRepository;

@Service
@RequiredArgsConstructor
public class LocalUpsertService {

    private final RcnLocalRepository repository;

    @Transactional
    public RcnLocal upsert(RcnLocalDto dto) {
        RcnLocal local = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnLocal(dto.gmlId()));

        local.setLocalNumber(dto.localId());
        local.setUsableArea(dto.usableArea());
        local.setAddressRef(dto.addressRef());

        return repository.save(local);
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnLocal gmlId is required");
        }
        return gmlId;
    }
}
