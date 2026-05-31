package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnParcel;
import pk.backend.infrastructure.dto.rcn.RcnParcelDto;
import pk.backend.infrastructure.repository.RcnParcelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParcelUpsertService {

    private final RcnParcelRepository repository;

    @Transactional
    public RcnParcel upsert(RcnParcelDto dto) {
        RcnParcel parcel = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnParcel(dto.gmlId()));

        parcel.setParcelId(dto.parcelId());
        parcel.setPrecinct(dto.zoning());
        parcel.setAddressRef(firstRef(dto.addressRefs()));
        parcel.setGeometryText(dto.geometryText());

        return repository.save(parcel);
    }

    private String requiredGmlId(String gmlId) {
        if (gmlId == null || gmlId.isBlank()) {
            throw new IllegalArgumentException("RcnParcel gmlId is required");
        }
        return gmlId;
    }

    private String firstRef(List<String> refs) {
        return refs == null || refs.isEmpty() ? null : refs.getFirst();
    }
}
