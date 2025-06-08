package devtitans.antoshchuk.devfusion2025backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for updating the status of a job application (JobPostActivity)")
public class JobPostActivityStatusUpdateRequestDTO {
    // @NotNull
    @Schema(description = "Job application activity ID (JobPostActivity)", example = "123")
    private Integer activityId;

    // @NotBlank
    @Schema(description = "New status for the job application", example = "Reviewed")
    private String status;
} 