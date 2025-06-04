package devtitans.antoshchuk.devfusion2025backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "search_history")
public class SearchHistory {
    @Id
    private String id;
    
    private Long userId;
    private String searchQuery;
    private List<String> tags;
    private List<String> skills;
    private String jobType;
    private String jobGradation;
    private LocalDateTime searchTimestamp;
    
    // Constructor for search queries
    public SearchHistory(Long userId, String searchQuery, List<String> tags, List<String> skills, 
                        String jobType, String jobGradation) {
        this.userId = userId;
        this.searchQuery = searchQuery;
        this.tags = tags;
        this.skills = skills;
        this.jobType = jobType;
        this.jobGradation = jobGradation;
        this.searchTimestamp = LocalDateTime.now();
    }
} 