package itq.test.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "approval_registry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false, unique = true)
    private Long documentId;

    @Column(name = "approved_by", nullable = false)
    private String approvedBy;

    @Column(name = "approved_at", insertable = false, updatable = false)
    private OffsetDateTime approvedAt;
}