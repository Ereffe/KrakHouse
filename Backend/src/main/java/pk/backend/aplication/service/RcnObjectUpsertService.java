package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.infrastructure.dto.rcn.RcnAddressDto;
import pk.backend.infrastructure.dto.rcn.RcnBuildingDto;
import pk.backend.infrastructure.dto.rcn.RcnLocalDto;
import pk.backend.infrastructure.dto.rcn.RcnObjectDto;
import pk.backend.infrastructure.dto.rcn.RcnParcelDto;
import pk.backend.infrastructure.dto.rcn.RcnPropertyDto;
import pk.backend.infrastructure.dto.rcn.RcnTransactionDto;

@Service
@RequiredArgsConstructor
public class RcnObjectUpsertService {

    private final TransactionUpsertService transactionUpsertService;
    private final PropertyUpsertService propertyUpsertService;
    private final LocalUpsertService localUpsertService;
    private final ParcelUpsertService parcelUpsertService;
    private final BuildingUpsertService buildingUpsertService;
    private final AddressUpsertService addressUpsertService;

    public void upsert(RcnObjectDto dto) {
        if (dto instanceof RcnTransactionDto transactionDto) {
            transactionUpsertService.upsert(transactionDto);
        } else if (dto instanceof RcnPropertyDto propertyDto) {
            propertyUpsertService.upsert(propertyDto);
        } else if (dto instanceof RcnLocalDto localDto) {
            localUpsertService.upsert(localDto);
        } else if (dto instanceof RcnParcelDto parcelDto) {
            parcelUpsertService.upsert(parcelDto);
        } else if (dto instanceof RcnBuildingDto buildingDto) {
            buildingUpsertService.upsert(buildingDto);
        } else if (dto instanceof RcnAddressDto addressDto) {
            addressUpsertService.upsert(addressDto);
        } else {
            throw new IllegalArgumentException("Unsupported RCN DTO type: " + dto.getClass().getName());
        }
    }
}
