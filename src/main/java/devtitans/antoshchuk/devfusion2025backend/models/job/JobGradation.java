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
@Table(name = "job_gradation")
public class JobGradation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "gradation")
    private String gradation;

    @OneToMany(mappedBy = "jobGradation")
    private List<JobPost> jobPosts;


    public JobGradation(String gradation) {
        this.gradation = gradation;
    }

    public JobGradation(String gradation, List<JobPost> jobPosts) {
        this.gradation = gradation;
        this.jobPosts = jobPosts;
    }
}
