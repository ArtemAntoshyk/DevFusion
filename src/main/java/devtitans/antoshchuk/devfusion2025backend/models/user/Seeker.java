package devtitans.antoshchuk.devfusion2025backend.models.user;

import jakarta.persistence.*;
import devtitans.antoshchuk.devfusion2025backend.models.job.JobPostActivity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "seeker")
public class Seeker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount userAccount;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "current_monthly_salary")
    private double currentMonthlySalary;

    @OneToMany(mappedBy = "seeker")
    private List<SeekerSkillSet> seekerSkillSets;

    @OneToMany(mappedBy = "seeker")
    private List<EducationDetail> educationDetails;

    @OneToMany(mappedBy = "seeker")
    private List<ExperienceDetail> experienceDetails;

    @OneToMany(mappedBy = "seeker")
    private List<JobPostActivity> jobPostActivities;

    @Column(name = "cv_url", length = Integer.MAX_VALUE)
    private String cvUrl;

    public Seeker(double currentMonthlySalary, Date dateOfBirth, String lastName, String firstName) {
        this.currentMonthlySalary = currentMonthlySalary;
        this.dateOfBirth = dateOfBirth;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "Seeker{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", currentMonthlySalary=" + currentMonthlySalary +
                '}';
    }
}
