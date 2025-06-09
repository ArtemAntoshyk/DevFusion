package devtitans.antoshchuk.devfusion2025backend.specifications;

import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.SeekerSkillSet;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SeekerSpecification {
    public static Specification<Seeker> filterByQueryAndSkills(String query, List<Integer> skillIds) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !query.isBlank()) {
                String likeQuery = "%" + query.toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("firstName")), likeQuery),
                                cb.like(cb.lower(root.get("lastName")), likeQuery),
                                cb.like(cb.lower(root.get("seekerTitle")), likeQuery)
                        )
                );
            }

            if (!CollectionUtils.isEmpty(skillIds)) {
                Join<Seeker, SeekerSkillSet> skillJoin = root.join("seekerSkillSets");
                predicates.add(skillJoin.get("skill").get("id").in(skillIds));
                cq.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 