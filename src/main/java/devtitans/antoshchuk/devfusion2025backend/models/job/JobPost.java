package devtitans.antoshchuk.devfusion2025backend.models.job;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "job_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "job_type_id", referencedColumnName = "id")
    private JobType jobType;

    @ManyToOne
    @JoinColumn(name = "job_gradation_id", referencedColumnName = "id")
    private JobGradation jobGradation;

    @Column(name = "title")
    private String title;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "job_description_en")
    private String jobDescriptionEn;

    @Column(name = "job_location")
    private String jobLocation;

    @Column(name = "is_company_name_hidden")
    private boolean isCompanyNameHidden;

    @Column(name = "created_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateTime;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "salary", length = 40)
    private String salary;

    @Column(name = "language", length = 100)
    private String language;

    @OneToMany(mappedBy = "jobPost")
    private List<JobPostActivity> jobPostActivities;
}
