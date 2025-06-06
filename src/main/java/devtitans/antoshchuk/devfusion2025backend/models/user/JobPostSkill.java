package devtitans.antoshchuk.devfusion2025backend.models.user;

import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "job_post_skill_set")
public class JobPostSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "skill_set_id", referencedColumnName = "id")
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;


    @Column(name = "skill_level")
    private Integer skillLevel;

}
