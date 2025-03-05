package devtitans.antoshchuk.devfusion2025backend.models.job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "job_type")
public class JobType {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "jobType")
    private List<JobPost> jobPosts;


    public JobType(String type) {
        this.type = type;
    }

    public JobType(String type, List<JobPost> jobPosts) {
        this.type = type;
        this.jobPosts = jobPosts;
    }
}
