package devtitans.antoshchuk.devfusion2025backend.models.user;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "company_logo")
    private String logo;

    @Column(name = "business_stream_name", nullable = false)
    private String businessStreamName;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    @Column(name = "company_website_url")
    private String companyWebsiteUrl;

    @Column(name = "establishment_date")
    private LocalDate establishmentDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<JobPost> jobPosts = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyImage> companyImages = new LinkedHashSet<>();

    public Company(String name, String logo, String businessStreamName, String companyDescription) {
        this.name = name;
        this.logo = logo;
        this.businessStreamName = businessStreamName;
        this.companyDescription = companyDescription;
    }

    public void addCompanyImage(CompanyImage image) {
        companyImages.add(image);
        image.setCompany(this);
    }

    public void removeCompanyImage(CompanyImage image) {
        companyImages.remove(image);
        image.setCompany(null);
    }
}
