package devtitans.antoshchuk.devfusion2025backend.models.user;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
@Getter
@Setter
public class Company {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @Column(name = "name")
    private String name;
    @Column(name = "business_stream_name")
    private String businessStreamName;
    @Column(name = "company_logo")
    private String companyLogo;
    @Column(name = "company_description")
    private String companyDescription;
    @Column(name = "establishment_date")
    private Date establishmentDate;
    @Column(name = "company_website_url")
    private String companyWebsiteUrl;

    @OneToMany(mappedBy = "company")
    private List<CompanyImage> companyImages;

    @OneToMany(mappedBy = "company", fetch = FetchType.EAGER)
    private List<JobPost> jobPosts;

    public Company(UserAccount user, String name, String businessStreamName, String companyLogo, String companyDescription, Date establishmentDate, String companyWebsiteUrl, List<CompanyImage> companyImages, List<JobPost> jobPosts) {
        this.user = user;
        this.name = name;
        this.businessStreamName = businessStreamName;
        this.companyLogo = companyLogo;
        this.companyDescription = companyDescription;
        this.establishmentDate = establishmentDate;
        this.companyWebsiteUrl = companyWebsiteUrl;
        this.companyImages = companyImages;
        this.jobPosts = jobPosts;
    }
}
