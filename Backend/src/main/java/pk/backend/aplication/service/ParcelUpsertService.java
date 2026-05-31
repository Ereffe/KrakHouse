package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnParcel;
import pk.backend.infrastructure.dto.rcn.RcnParcelDto;
import pk.backend.infrastructure.repository.RcnParcelRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParcelUpsertService {

    private final RcnParcelRepository repository;
    private final RcnReferenceCandidateService referenceCandidateService;

    @Transactional
    public RcnParcel upsert(RcnParcelDto dto) {
        RcnParcel parcel = repository.findByGmlId(requiredGmlId(dto.gmlId()))
                .orElseGet(() -> new RcnParcel(dto.gmlId()));

        parcel.setParcelId(dto.parcelId());
        parcel.setPrecinct(dto.zoning());
        parcel.setAddressRef(firstRef(dto.addressRefs()));
        parcel.setGeometryText(dto.geometryText());

        RcnParcel saved = repository.save(parcel);
        referenceCandidateService.replaceReferences(
                "RCN_Dzialka",
                saved.getGmlId(),
                references(dto.addressRefs())
        );

        return saved;
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

    private List<RcnReferenceCandidateService.ReferenceCandidate> references(List<String> addressRefs) {
        List<RcnReferenceCandidateService.ReferenceCandidate> references = new ArrayList<>();

        if (addressRefs == null) {
            return references;
        }

        for (String addressRef : addressRefs) {
            references.add(new RcnReferenceCandidateService.ReferenceCandidate("adresDzialki", addressRef));
        }

        return references;
    }
}
