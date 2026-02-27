package itq.test.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class DocumentListRequest {
    private List<Long> docs;
}
