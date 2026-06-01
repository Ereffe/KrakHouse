package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnBuilding;
import pk.backend.infrastructure.dto.rcn.RcnBuildingDto;
import pk.backend.infrastructure.repository.RcnBuildingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingUpsertService {

    private final RcnBuildingRepository repository;
    private final RcnReferenceCandidateService referenceCandidateService;

    @Transactional
    public RcnBuilding upsert(RcnBuildingDto dto) {
        RcnBuilding building = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnBuilding(dto.gmlId()));

        building.setBuildingId(dto.buildingId());
        building.setBuildingType(dto.buildingType());
        building.setAddressRef(dto.addressRef());
        building.setGeometryText(dto.geometryText());
        building.setCenterX(dto.centerX());
        building.setCenterY(dto.centerY());
        building.setSrid(dto.srid());

        RcnBuilding saved = repository.save(building);
        referenceCandidateService.replaceReferences(
                "RCN_Budynek",
                saved.getGmlId(),
                List.of(new RcnReferenceCandidateService.ReferenceCandidate("adresBudynku", dto.addressRef()))
        );

        return saved;
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnBuilding gmlId is required");
        }
        return gmlId;
    }
}
