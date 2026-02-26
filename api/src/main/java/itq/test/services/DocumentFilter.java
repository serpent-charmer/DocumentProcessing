package itq.test.services;

import itq.test.dto.DocumentFilterSearchRequest;
import itq.test.entities.Document;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentFilter {
    public static Specification<Document> createdBetween(DocumentFilterSearchRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getAuthor() != null && !filter.getAuthor().isEmpty()) {
                predicates.add(cb.equal(root.get("author"), filter.getAuthor()));
            }

            if (filter.getAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getAfter()));
            }

            if (filter.getBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

