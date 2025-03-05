package devtitans.antoshchuk.devfusion2025backend.models.user;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "company_image")
public class CompanyImage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;
    @Column(name = "company_image")
    private String company_image;

    public CompanyImage(Company company, String company_image) {
        this.company = company;
        this.company_image = company_image;
    }
}
