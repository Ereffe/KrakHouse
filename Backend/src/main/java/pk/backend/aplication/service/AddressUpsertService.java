package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnAddress;
import pk.backend.infrastructure.dto.rcn.RcnAddressDto;
import pk.backend.infrastructure.repository.RcnAddressRepository;

@Service
@RequiredArgsConstructor
public class AddressUpsertService {

    private final RcnAddressRepository repository;

    @Transactional
    public RcnAddress upsert(RcnAddressDto dto) {
        RcnAddress address = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnAddress(dto.gmlId()));

        address.setCity(dto.city());
        address.setStreet(dto.street());
        address.setBuildingNumber(dto.buildingNumber());

        return repository.save(address);
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnAddress gmlId is required");
        }
        return gmlId;
    }
}
