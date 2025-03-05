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
@Table(name = "education_detail")
public class EducationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", referencedColumnName = "id")
    private Seeker seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_degree_id", referencedColumnName = "id")
    private CertificateDegree certificateDegree;
    @Column(name = "major")
    private String major;
    @Column(name = "institute_university_name")
    private String instituteOrUniversityName;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "completion_date")
    @Temporal(TemporalType.DATE)

    private Date completionDate;
    @Column(name = "cgpa")
    private int cgpa;

    public EducationDetail(int cgpa, Date completionDate, Date startDate, String major) {
        this.cgpa = cgpa;
        this.completionDate = completionDate;
        this.startDate = startDate;
        this.major = major;
    }

    public EducationDetail(Seeker seeker, CertificateDegree certificateDegree, String major, String instituteOrUniversityName, Date startDate, Date completionDate, int cgpa) {
        this.seeker = seeker;
        this.certificateDegree = certificateDegree;
        this.major = major;
        this.instituteOrUniversityName = instituteOrUniversityName;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.cgpa = cgpa;
    }
}
