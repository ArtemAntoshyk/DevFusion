package devtitans.antoshchuk.devfusion2025backend.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "certificate_degree")
public class CertificateDegree {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "certificateDegree")
    private List<EducationDetail> certificates;

    public CertificateDegree(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CertificateDegree{" +
                "name='" + name + '\'' +
                '}';
    }
}
