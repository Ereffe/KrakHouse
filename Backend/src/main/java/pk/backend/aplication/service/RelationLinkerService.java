package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnAddress;
import pk.backend.domain.model.rcn.RcnBuilding;
import pk.backend.domain.model.rcn.RcnLocal;
import pk.backend.domain.model.rcn.RcnParcel;
import pk.backend.domain.model.rcn.RcnProperty;
import pk.backend.domain.model.rcn.RcnTransaction;
import pk.backend.domain.model.rcn.RcnUnresolvedReference;
import pk.backend.infrastructure.repository.RcnAddressRepository;
import pk.backend.infrastructure.repository.RcnBuildingRepository;
import pk.backend.infrastructure.repository.RcnLocalRepository;
import pk.backend.infrastructure.repository.RcnParcelRepository;
import pk.backend.infrastructure.repository.RcnPropertyRepository;
import pk.backend.infrastructure.repository.RcnTransactionRepository;
import pk.backend.infrastructure.repository.RcnUnresolvedReferenceRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelationLinkerService {

    private static final String TRANSACTION = "RCN_Transakcja";
    private static final String PROPERTY = "RCN_Nieruchomosc";
    private static final String LOCAL = "RCN_Lokal";
    private static final String PARCEL = "RCN_Dzialka";
    private static final String BUILDING = "RCN_Budynek";

    private final RcnUnresolvedReferenceRepository referenceRepository;
    private final RcnTransactionRepository transactionRepository;
    private final RcnPropertyRepository propertyRepository;
    private final RcnLocalRepository localRepository;
    private final RcnParcelRepository parcelRepository;
    private final RcnBuildingRepository buildingRepository;
    private final RcnAddressRepository addressRepository;

    @Transactional
    public RelationLinkResult linkAll() {
        LinkCounter counter = new LinkCounter();

        linkTransactionToProperty(counter);
        linkPropertyToParcel(counter);
        linkPropertyToBuilding(counter);
        linkPropertyToLocal(counter);
        linkLocalToAddress(counter);
        linkParcelToAddress(counter);
        linkBuildingToAddress(counter);

        RelationLinkResult result = counter.toResult();
        log.info("Finished RCN relation linking: linked={}, missingSources={}, missingTargets={}",
                result.linkedRelations(), result.missingSources(), result.missingTargets());
        return result;
    }

    private void linkTransactionToProperty(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(TRANSACTION, "nieruchomosc")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnTransaction> transaction = transactionRepository.findByGmlId(reference.getSourceGmlId());
            if (transaction.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnProperty> property = propertyRepository.findByGmlId(reference.getTargetGmlId());
            if (property.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            transaction.get().setProperty(property.get());
            transactionRepository.save(transaction.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkPropertyToParcel(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(PROPERTY, "dzialka")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnProperty> property = propertyRepository.findByGmlId(reference.getSourceGmlId());
            if (property.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnParcel> parcel = parcelRepository.findByGmlId(reference.getTargetGmlId());
            if (parcel.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            property.get().setParcel(parcel.get());
            propertyRepository.save(property.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkPropertyToBuilding(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(PROPERTY, "budynek")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnProperty> property = propertyRepository.findByGmlId(reference.getSourceGmlId());
            if (property.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnBuilding> building = buildingRepository.findByGmlId(reference.getTargetGmlId());
            if (building.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            property.get().setBuilding(building.get());
            propertyRepository.save(property.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkPropertyToLocal(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(PROPERTY, "lokal")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnProperty> property = propertyRepository.findByGmlId(reference.getSourceGmlId());
            if (property.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnLocal> local = localRepository.findByGmlId(reference.getTargetGmlId());
            if (local.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            property.get().setLocal(local.get());
            propertyRepository.save(property.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkLocalToAddress(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(LOCAL, "adresBudynkuZLokalem")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnLocal> local = localRepository.findByGmlId(reference.getSourceGmlId());
            if (local.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnAddress> address = addressRepository.findByGmlId(reference.getTargetGmlId());
            if (address.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            local.get().setAddress(address.get());
            localRepository.save(local.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkParcelToAddress(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(PARCEL, "adresDzialki")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnParcel> parcel = parcelRepository.findByGmlId(reference.getSourceGmlId());
            if (parcel.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnAddress> address = addressRepository.findByGmlId(reference.getTargetGmlId());
            if (address.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            parcel.get().setAddress(address.get());
            parcelRepository.save(parcel.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private void linkBuildingToAddress(LinkCounter counter) {
        Set<String> linkedSources = new HashSet<>();

        for (RcnUnresolvedReference reference : references(BUILDING, "adresBudynku")) {
            if (linkedSources.contains(reference.getSourceGmlId())) {
                continue;
            }

            Optional<RcnBuilding> building = buildingRepository.findByGmlId(reference.getSourceGmlId());
            if (building.isEmpty()) {
                counter.missingSource();
                linkedSources.add(reference.getSourceGmlId());
                continue;
            }

            Optional<RcnAddress> address = addressRepository.findByGmlId(reference.getTargetGmlId());
            if (address.isEmpty()) {
                counter.missingTarget();
                continue;
            }

            building.get().setAddress(address.get());
            buildingRepository.save(building.get());
            counter.linked();
            linkedSources.add(reference.getSourceGmlId());
        }
    }

    private Iterable<RcnUnresolvedReference> references(String sourceType, String relationName) {
        return referenceRepository.findBySourceTypeAndRelationNameOrderByIdAsc(sourceType, relationName);
    }

    private static class LinkCounter {
        private long linkedRelations;
        private long missingSources;
        private long missingTargets;

        void linked() {
            linkedRelations++;
        }

        void missingSource() {
            missingSources++;
        }

        void missingTarget() {
            missingTargets++;
        }

        RelationLinkResult toResult() {
            return new RelationLinkResult(linkedRelations, missingSources, missingTargets);
        }
    }
}
