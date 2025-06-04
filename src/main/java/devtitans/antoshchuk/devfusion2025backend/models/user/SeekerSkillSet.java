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
@Table(name = "seeker_skill_set")
public class SeekerSkillSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", referencedColumnName = "id")
    private Seeker seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_set_id", referencedColumnName = "id")
    private Skill skill;

    @Column(name = "skill_level")
    private Short skillLevel;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Override
    public String toString() {
        return "SeekerSkillSet{" +
                "id=" + id +
                '}';
    }
}
