package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pk.backend.domain.model.rcn.RcnUnresolvedReference;
import pk.backend.infrastructure.repository.RcnUnresolvedReferenceRepository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RcnReferenceCandidateService {

    private final RcnUnresolvedReferenceRepository repository;

    @Transactional
    public void replaceReferences(String sourceType, String sourceGmlId, Collection<ReferenceCandidate> references) {
        if (sourceGmlId == null || sourceGmlId.isBlank()) {
            return;
        }

        repository.deleteBySourceTypeAndSourceGmlId(sourceType, sourceGmlId);

        List<RcnUnresolvedReference> unresolvedReferences = normalized(references).stream()
                .map(reference -> new RcnUnresolvedReference(
                        sourceType,
                        sourceGmlId,
                        reference.relationName(),
                        reference.targetGmlId()
                ))
                .toList();

        repository.saveAll(unresolvedReferences);
    }

    private Set<ReferenceCandidate> normalized(Collection<ReferenceCandidate> references) {
        Set<ReferenceCandidate> normalized = new LinkedHashSet<>();

        if (references == null) {
            return normalized;
        }

        for (ReferenceCandidate reference : references) {
            if (reference == null || isBlank(reference.relationName()) || isBlank(reference.targetGmlId())) {
                continue;
            }

            normalized.add(new ReferenceCandidate(
                    reference.relationName().trim(),
                    normalizeHref(reference.targetGmlId())
            ));
        }

        return normalized;
    }

    private String normalizeHref(String href) {
        String trimmed = href.trim();
        return trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record ReferenceCandidate(String relationName, String targetGmlId) {
    }
}
