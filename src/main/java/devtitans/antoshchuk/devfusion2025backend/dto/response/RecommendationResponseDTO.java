package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponseDTO {
    private List<JobPostResponseDTO> jobs;
    private int page;
    private int size;
    private int total;
} 