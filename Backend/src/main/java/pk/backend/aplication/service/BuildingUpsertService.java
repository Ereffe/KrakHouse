package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnBuilding;
import pk.backend.infrastructure.dto.rcn.RcnBuildingDto;
import pk.backend.infrastructure.repository.RcnBuildingRepository;

@Service
@RequiredArgsConstructor
public class BuildingUpsertService {

    private final RcnBuildingRepository repository;

    @Transactional
    public RcnBuilding upsert(RcnBuildingDto dto) {
        RcnBuilding building = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnBuilding(dto.gmlId()));

        building.setBuildingId(dto.buildingId());
        building.setBuildingType(dto.buildingType());
        building.setAddressRef(dto.addressRef());
        building.setGeometryText(dto.geometryText());

        return repository.save(building);
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnBuilding gmlId is required");
        }
        return gmlId;
    }
}
