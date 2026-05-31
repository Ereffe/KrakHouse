package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnProperty;
import pk.backend.infrastructure.dto.rcn.RcnPropertyDto;
import pk.backend.infrastructure.repository.RcnPropertyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyUpsertService {

    private final RcnPropertyRepository repository;
    private final RcnReferenceCandidateService referenceCandidateService;

    @Transactional
    public RcnProperty upsert(RcnPropertyDto dto) {
        RcnProperty property = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnProperty(dto.gmlId()));

        property.setPropertyType(dto.propertyType());
        property.setParcelRef(firstRef(dto.parcelRefs()));
        property.setBuildingRef(firstRef(dto.buildingRefs()));
        property.setLocalRef(firstRef(dto.localRefs()));

        RcnProperty saved = repository.save(property);
        referenceCandidateService.replaceReferences(
                "RCN_Nieruchomosc",
                saved.getGmlId(),
                references(dto)
        );

        return saved;
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnProperty gmlId is required");
        }
        return gmlId;
    }

    private String firstRef(List<String> refs) {
        return refs == null || refs.isEmpty() ? null : refs.getFirst();
    }

    private List<RcnReferenceCandidateService.ReferenceCandidate> references(RcnPropertyDto dto) {
        List<RcnReferenceCandidateService.ReferenceCandidate> references = new ArrayList<>();
        addAll(references, "dzialka", dto.parcelRefs());
        addAll(references, "budynek", dto.buildingRefs());
        addAll(references, "lokal", dto.localRefs());
        return references;
    }

    private void addAll(
            List<RcnReferenceCandidateService.ReferenceCandidate> references,
            String relationName,
            List<String> targetGmlIds
    ) {
        if (targetGmlIds == null) {
            return;
        }

        for (String targetGmlId : targetGmlIds) {
            references.add(new RcnReferenceCandidateService.ReferenceCandidate(relationName, targetGmlId));
        }
    }
}
