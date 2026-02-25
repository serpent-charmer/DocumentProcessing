package itq.test.controllers;

import itq.test.dto.CreateDocumentRequest;
import itq.test.dto.DocumentHistoryResponse;
import itq.test.dto.FindDocumentHistoryRequest;
import itq.test.dto.DocumentListRequest;
import itq.test.entities.Document;
import itq.test.services.DocumentService;
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

    @PostMapping("/create")
    public ResponseEntity<String> create(@Valid @RequestBody CreateDocumentRequest request) {

        documentService.createDocument(
                    request.getTitle(),
                    request.getAuthor()
        );

        return new ResponseEntity<>("ok", HttpStatus.CREATED);
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
            page = 0,
            size = 20,
            sort = "createdAt",
            direction = Sort.Direction.ASC
    ) Pageable pageable) {
        var page = documentService.findDocuments(request.getDocs(), pageable);
        return new ResponseEntity<>(page.getContent(), HttpStatus.CREATED);
    }



    @PostMapping("/submit")
    public ResponseEntity<HashMap<Long, UpdateStatus>> submit(@Valid @RequestBody DocumentListRequest request) {
        var result = documentService.submitDocuments(request.getDocs());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/approve")
    public ResponseEntity<HashMap<Long, UpdateStatus>> approve(@Valid @RequestBody DocumentListRequest request) {
        var result = documentService.approveDocuments(request.getDocs());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
