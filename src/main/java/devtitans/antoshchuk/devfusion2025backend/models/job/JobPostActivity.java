package devtitans.antoshchuk.devfusion2025backend.models.job;

import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "job_post_activity")
public class JobPostActivity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "job_post_id", referencedColumnName = "id")
    private JobPost jobPost;

    @ManyToOne()
    @JoinColumn(name = "seeker_id", referencedColumnName = "id")
    private Seeker seeker;

    @Column(name = "apply_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyDate;

    @Size(max = 50)
    @ColumnDefault("'Очікує'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "decision_at")
    private Instant decisionAt;

    @Column(name = "comment", length = Integer.MAX_VALUE)
    private String comment;


    public JobPostActivity(JobPost jobPost, Seeker seeker, Date applyDate) {
        this.jobPost = jobPost;
        this.seeker = seeker;
        this.applyDate = applyDate;
    }
}
