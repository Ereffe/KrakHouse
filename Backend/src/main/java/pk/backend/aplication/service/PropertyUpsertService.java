package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnProperty;
import pk.backend.infrastructure.dto.rcn.RcnPropertyDto;
import pk.backend.infrastructure.repository.RcnPropertyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyUpsertService {

    private final RcnPropertyRepository repository;

    @Transactional
    public RcnProperty upsert(RcnPropertyDto dto) {
        RcnProperty property = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnProperty(dto.gmlId()));

        property.setPropertyType(dto.propertyType());
        property.setParcelRef(firstRef(dto.parcelRefs()));
        property.setBuildingRef(firstRef(dto.buildingRefs()));
        property.setLocalRef(firstRef(dto.localRefs()));

        return repository.save(property);
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
}
