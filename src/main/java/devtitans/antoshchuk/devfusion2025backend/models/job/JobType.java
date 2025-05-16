package devtitans.antoshchuk.devfusion2025backend.models.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "job_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private String name;

    @OneToMany(mappedBy = "jobType")
    private List<JobPost> jobPosts;

    public JobType(String name) {
        this.name = name;
    }

    public JobType(String name, List<JobPost> jobPosts) {
        this.name = name;
        this.jobPosts = jobPosts;
    }
}
