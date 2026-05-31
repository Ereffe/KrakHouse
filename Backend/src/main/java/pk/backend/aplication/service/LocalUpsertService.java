package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnLocal;
import pk.backend.infrastructure.dto.rcn.RcnLocalDto;
import pk.backend.infrastructure.repository.RcnLocalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalUpsertService {

    private final RcnLocalRepository repository;
    private final RcnReferenceCandidateService referenceCandidateService;

    @Transactional
    public RcnLocal upsert(RcnLocalDto dto) {
        RcnLocal local = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnLocal(dto.gmlId()));

        local.setLocalNumber(dto.localId());
        local.setUsableArea(dto.usableArea());
        local.setAddressRef(dto.addressRef());

        RcnLocal saved = repository.save(local);
        referenceCandidateService.replaceReferences(
                "RCN_Lokal",
                saved.getGmlId(),
                List.of(new RcnReferenceCandidateService.ReferenceCandidate("adresBudynkuZLokalem", dto.addressRef()))
        );

        return saved;
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnLocal gmlId is required");
        }
        return gmlId;
    }
}
