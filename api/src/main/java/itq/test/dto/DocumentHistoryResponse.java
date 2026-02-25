package itq.test.dto;

import itq.test.entities.Document;
import itq.test.entities.DocumentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DocumentHistoryResponse {
    Document document;
    List<DocumentHistory> history;
}
