package devtitans.antoshchuk.devfusion2025backend.models.job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "job_gradation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobGradation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "gradation")
    private String name;

    @OneToMany(mappedBy = "jobGradation")
    private List<JobPost> jobPosts;

    public JobGradation(String name) {
        this.name = name;
    }

    public JobGradation(String name, List<JobPost> jobPosts) {
        this.name = name;
        this.jobPosts = jobPosts;
    }
}
