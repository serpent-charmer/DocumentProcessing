package itq.test.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DocumentListRequest {

    @NotNull
    private List<Long> docs;

}
