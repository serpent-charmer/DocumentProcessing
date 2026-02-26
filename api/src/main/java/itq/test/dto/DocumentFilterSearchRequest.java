package itq.test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import itq.test.entities.enums.DocumentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class DocumentFilterSearchRequest {
    private DocumentStatus status;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate after;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate before;
}
