package devtitans.antoshchuk.devfusion2025backend.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_type_id", referencedColumnName = "id")
    private UserType userType;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "contact_number")
    private String contactNumber;
    @Column(name = "user_image")
    private String userImage;
    @Column(name = "is_active")
    private boolean active;
    @Column(name = "email_notification_active")
    private boolean emailNotificationActive;
    @Column(name = "registration_date")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;
    @OneToOne(mappedBy = "userAccount")
    @JsonIgnore
    private Seeker seeker;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Company company;

    public UserAccount(UserType userType,  String email, String password, String contactNumber, String userImage, boolean active, boolean emailNotificationActive, Date registrationDate) {
        this.userType = userType;

        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.userImage = userImage;
        this.active = active;
        this.emailNotificationActive = emailNotificationActive;
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "id=" + id +
                ", userType=" + userType +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", userImage='" + userImage + '\'' +
                ", active=" + active +
                ", emailNotificationActive=" + emailNotificationActive +
                ", registrationDate=" + registrationDate +
                ", seeker=" + seeker +
                ", company=" + company +
                '}';
    }
}
