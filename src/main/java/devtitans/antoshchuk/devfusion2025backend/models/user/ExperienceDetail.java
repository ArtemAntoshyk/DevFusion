package devtitans.antoshchuk.devfusion2025backend.models.user;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "experience_detail")
public class ExperienceDetail {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", referencedColumnName = "id")
    private Seeker seeker;

    @Column(name = "is_current_job")
    private boolean isCurrentJob;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "job_location_city")
    private String jobLocationCity;
    @Column(name = "job_location_country")
    private String jobLocationCounty;
    @Column(name = "description")
    private String description;

    public ExperienceDetail(Seeker seeker, boolean isCurrentJob, Date startDate, Date endDate, String jobTitle, String companyName, String jobLocationCity, String jobLocationCounty, String description) {
        this.seeker = seeker;
        this.isCurrentJob = isCurrentJob;
        this.startDate = startDate;
        this.endDate = endDate;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.jobLocationCity = jobLocationCity;
        this.jobLocationCounty = jobLocationCounty;
        this.description = description;
    }

}
