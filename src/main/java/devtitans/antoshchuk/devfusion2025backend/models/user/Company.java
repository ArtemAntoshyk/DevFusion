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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "logo_url")
    private String logo;

    @Column(name = "business_stream_name")
    private String businessStreamName;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @OneToMany(mappedBy = "company")
    private List<JobPost> jobPosts;

    public Company(String name, String logo, String businessStreamName, String companyDescription) {
        this.name = name;
        this.logo = logo;
        this.businessStreamName = businessStreamName;
        this.companyDescription = companyDescription;
    }
}
