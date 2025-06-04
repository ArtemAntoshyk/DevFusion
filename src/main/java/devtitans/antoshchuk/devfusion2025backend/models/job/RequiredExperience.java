package devtitans.antoshchuk.devfusion2025backend.models.job;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "required_experience")
public class RequiredExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 55)
    @Column(name = "experience", length = 55)
    private String experience;

    @OneToMany(mappedBy = "experience")
    private Set<JobPost> jobPosts = new LinkedHashSet<>();

}