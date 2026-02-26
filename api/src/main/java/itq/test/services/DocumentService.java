package itq.test.services;

import itq.test.dto.DocumentFilterSearchRequest;
import itq.test.entities.ApprovalRegistry;
import itq.test.entities.Document;
import itq.test.entities.DocumentHistory;
import itq.test.entities.enums.ActionType;
import itq.test.entities.enums.DocumentStatus;
import itq.test.repositories.ApprovalRepository;
import itq.test.repositories.DocumentHistoryRepository;
import itq.test.repositories.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final DocumentHistoryRepository documentHistory;
    private final ApprovalRepository approvalRepository;

    @Transactional
    public void createDocument(String title, String author) {
        Document entity = new Document();
        entity.setTitle(title);
        entity.setAuthor(author);
        entity.setStatus(DocumentStatus.DRAFT);

        documentRepository.save(entity);
    }

    public List<DocumentHistory> findHistory(Long doc) {
        return documentHistory.findAllByDocumentIdOrderByCreatedAtDesc(doc);
    }

    public Page<Document> findDocuments(List<Long> docs, Pageable pageable) {
        return documentRepository.findByIdIn(docs, pageable);
    }

    public HashMap<Long, UpdateStatus> submitDocuments(List<Long> docs) {
        var otherDocs = documentRepository.findAllById(docs);
        var processed = new HashMap<Long, UpdateStatus>();
        for (Document doc : otherDocs) {
            processed.put(doc.getId(), UpdateStatus.SUCCESS);
            if (doc.getStatus() != DocumentStatus.DRAFT) {
                processed.replace(doc.getId(), UpdateStatus.CONFLICT);
                continue;
            }
            try {
                submitDocument(doc);
            } catch (Exception e) {
                log.error("Exception", e);
                processed.replace(doc.getId(), UpdateStatus.ERROR);
            }
        }
        docs.forEach(k -> {
            if(!processed.containsKey(k))
                processed.put(k, UpdateStatus.NOT_FOUND);
        });
        return processed;
    }

    @Transactional
    private void submitDocument(Document doc) {
        doc.setStatus(DocumentStatus.SUBMITTED);
        var docHistory = new DocumentHistory();
        docHistory.setDocumentId(doc.getId());
        docHistory.setAction(ActionType.SUBMIT);
        documentHistory.save(docHistory);
        documentRepository.save(doc);
    }


    public HashMap<Long, UpdateStatus> approveDocuments(List<Long> docs) {
        var otherDocs = documentRepository.findAllById(docs);
        var processed = new HashMap<Long, UpdateStatus>();
        for (Document doc : otherDocs) {
            processed.put(doc.getId(), UpdateStatus.SUCCESS);
            if (doc.getStatus() != DocumentStatus.SUBMITTED) {
                processed.replace(doc.getId(), UpdateStatus.CONFLICT);
                continue;
            }
            try {
                approveDocument(doc);
            } catch (Exception e) {
                log.error("Exception", e);
                processed.replace(doc.getId(), UpdateStatus.ERROR);
            }
        }

        docs.forEach(k -> {
            if(!processed.containsKey(k))
                processed.put(k, UpdateStatus.NOT_FOUND);
        });
        return processed;
    }

    @Transactional
    private void approveDocument(Document doc) {
        doc.setStatus(DocumentStatus.APPROVED);
        var docHistory = new DocumentHistory();
        docHistory.setDocumentId(doc.getId());
        docHistory.setAction(ActionType.APPROVE);

        var approval = new ApprovalRegistry();
        approval.setDocumentId(doc.getId());
        approval.setApprovedBy("somemail@mail.com");
        approvalRepository.save(approval);
        documentHistory.save(docHistory);
        documentRepository.save(doc);
    }

    public UpdateStatus approveDocument(long id) {
        var wDoc = documentRepository.findById(id);

        if(wDoc.isEmpty())
            return UpdateStatus.NOT_FOUND;

        var doc = wDoc.get();

        if (doc.getStatus() != DocumentStatus.SUBMITTED) {
            return UpdateStatus.CONFLICT;
        }

        try {
            approveDocument(doc);
        } catch (Exception e) {
            log.error("Exception", e);
            return UpdateStatus.ERROR;
        }

        return UpdateStatus.SUCCESS;
    }

    public List<Document> searchFilters(DocumentFilterSearchRequest filters) {
        return documentRepository.findAll(Specification.allOf(DocumentFilter.createdBetween(filters)));
    }

}
