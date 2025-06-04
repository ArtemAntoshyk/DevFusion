package devtitans.antoshchuk.devfusion2025backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "job_view_history")
public class JobViewHistory {
    @Id
    private String id;
    
    private Long userId;
    private Long jobPostId;
    private String jobTitle;
    private String companyName;
    private LocalDateTime viewTimestamp;
    
    public JobViewHistory(Long userId, Long jobPostId, String jobTitle, String companyName) {
        this.userId = userId;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.viewTimestamp = LocalDateTime.now();
    }
} 