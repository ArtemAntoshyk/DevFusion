package devtitans.antoshchuk.devfusion2025backend.dto.request;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilteredRecommendRequest {
    private String query;
    private List<Integer> jobTypeIds;
    private List<Integer> gradationIds;
    private List<Integer> tagIds;
    private Integer page;
    private Integer size;
}