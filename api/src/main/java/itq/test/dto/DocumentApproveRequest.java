package itq.test.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentApproveRequest {
    private long id;
    @NotNull
    @DecimalMin("1")
    private int threads;
    @NotNull
    @DecimalMin("1")
    private int attempts;
}
