package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationRequestDTO {
    private String query;
    private Integer topK;
    private Double threshold;
    private List<Integer> categoryIds;
    private List<Integer> tagIds;
    private Integer jobPostId;
    private Integer lastViewedJobPostId;
    private Integer page = 0;
    private Integer size = 10;
} 