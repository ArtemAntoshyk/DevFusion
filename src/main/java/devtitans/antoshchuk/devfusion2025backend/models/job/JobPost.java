package devtitans.antoshchuk.devfusion2025backend.models.job;

import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.JobPostSkill;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "job_type_id", referencedColumnName = "id")
    private JobType jobType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "job_gradation_id", referencedColumnName = "id")
    private JobGradation jobGradation;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "title_en", nullable = false)
    private String titleEn;

    @NotBlank
    @Column(name = "job_description", nullable = false)
    private String jobDescription;

    @NotBlank
    @Column(name = "job_description_en", nullable = false)
    private String jobDescriptionEn;

    @NotBlank
    @Column(name = "job_location", nullable = false)
    private String jobLocation;

    @Column(name = "is_company_name_hidden")
    private boolean isCompanyNameHidden = false;

    @NotNull
    @Column(name = "created_date_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateTime;

    @Column(name = "is_active")
    private boolean isActive = true;

    @NotBlank
    @Column(name = "salary", length = 40, nullable = false)
    private String salary;

    @NotBlank
    @Column(name = "language", length = 100, nullable = false)
    private String language;

    @OneToMany(mappedBy = "jobPost")
    private List<JobPostActivity> jobPostActivities;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "experience_id", nullable = false)
    private RequiredExperience experience;

    @OneToMany(mappedBy = "jobPost")
    private Set<JobPostSkill> jobPostSkillSets = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "job_post_tag",
            joinColumns = @JoinColumn(name = "job_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new LinkedHashSet<>();
}
