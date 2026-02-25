package itq.test.repositories;

import itq.test.entities.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {
    List<DocumentHistory> findAllByDocumentIdOrderByCreatedAtDesc(Long documentId);
}
