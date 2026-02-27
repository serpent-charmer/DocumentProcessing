package itq.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import itq.test.entities.Document;
import itq.test.entities.enums.DocumentStatus;
import itq.test.repositories.DocumentRepository;
import itq.test.service.dto.DocumentListRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SubmitBatchWorker {
    @Autowired
    private DocumentRepository repository;

    @Value("${worker.batchSize:1}")
    private int batchSize;

    private static final HttpClient client = HttpClient.newHttpClient();

    private final Logger log = LoggerFactory.getLogger(SubmitBatchWorker.class);
    private int count = 0;

    private final DocumentStatus DOCUMENT_STATUS = DocumentStatus.SUBMITTED;

    @Scheduled(fixedDelay = 5000)
    private void batch() {

        Pageable pageRequest = PageRequest.of(
                0,
                batchSize,
                Sort.by("createdAt").ascending()
        );

        var slice = repository.findByStatus(DOCUMENT_STATUS, pageRequest);
        var numOfDocs = slice.getTotalElements();
        if(numOfDocs == 0)
            return;
        log.info("Start processing {} documents", numOfDocs);
        count = 0;

        while(slice.hasContent()) {
            processPage(slice);
            if(!slice.hasNext()) break;
            slice = repository.findByStatus(DOCUMENT_STATUS, pageRequest);
        }
    }

    private void processPage(Slice<Document> page) {
        var data = new DocumentListRequest();
        data.setDocs(page.getContent().stream().map(Document::getId).toList());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8080/api/v1/document/approve"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Exception", e);
        }
        count += page.getContent().size();
        log.info("Changed status of {} documents", count);
    }
}