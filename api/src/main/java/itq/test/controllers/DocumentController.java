package itq.test.controllers;

import itq.test.dto.CreateDocumentRequest;
import itq.test.dto.DocumentApproveRequest;
import itq.test.dto.DocumentFilterSearchRequest;
import itq.test.dto.DocumentHistoryResponse;
import itq.test.dto.FindDocumentHistoryRequest;
import itq.test.dto.DocumentListRequest;
import itq.test.entities.Document;
import itq.test.services.DocumentService;
import itq.test.services.ParallelDocumentService;
import itq.test.services.UpdateStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final ParallelDocumentService parallelDocumentService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@Valid @RequestBody CreateDocumentRequest request) {

        documentService.createDocument(
                    request.getTitle(),
                    request.getAuthor()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/history")
    public ResponseEntity<DocumentHistoryResponse> history(@Valid @RequestBody FindDocumentHistoryRequest request) {
        var id = request.getDoc();
        var page = documentService.findDocuments(List.of(id), null);
        var history = documentService.findHistory(id);
        var docs = page.getContent();
        if(docs.size() == 1) {
            return new ResponseEntity<>(new DocumentHistoryResponse(docs.getFirst(), history), HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(404));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Document>> search(@Valid @RequestBody DocumentListRequest request, @PageableDefault(
            sort = "createdAt",
            direction = Sort.Direction.ASC
    ) Pageable pageable) {
        var page = documentService.findDocuments(request.getDocs(), pageable);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @PostMapping("/submit")
    public ResponseEntity<HashMap<Long, UpdateStatus>> submit(@Valid @RequestBody DocumentListRequest request) {
        var result = documentService.submitDocuments(request.getDocs());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/approve")
    public ResponseEntity<HashMap<Long, UpdateStatus>> approve(@Valid @RequestBody DocumentListRequest request) {
        var result = documentService.approveDocuments(request.getDocs());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Document>> searchFilters(@Valid @RequestBody DocumentFilterSearchRequest request) {
        return new ResponseEntity<>(documentService.searchFilters(request), HttpStatus.OK);
    }

    @PostMapping("/submitParallel")
    public ResponseEntity<List<UpdateStatus>> searchFilters(@Valid @RequestBody DocumentApproveRequest request) {
        return new ResponseEntity<>(
                parallelDocumentService.submitDoc(request.getId(), request.getThreads(), request.getAttempts()),
                HttpStatus.OK);
    }


}
