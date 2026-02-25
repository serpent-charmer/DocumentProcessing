package itq.test.repositories;

import itq.test.entities.ApprovalRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<ApprovalRegistry, Long> {
}

