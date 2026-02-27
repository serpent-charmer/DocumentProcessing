package itq.test.repositories;

import itq.test.entities.Document;
import itq.test.entities.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Page<Document> findByIdIn(List<Long> ids, Pageable pageable);
    Slice<Document> findByStatus(DocumentStatus status, Pageable pageable);
}
