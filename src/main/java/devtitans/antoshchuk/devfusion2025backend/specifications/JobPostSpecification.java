package devtitans.antoshchuk.devfusion2025backend.specifications;

import devtitans.antoshchuk.devfusion2025backend.models.job.JobPost;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class JobPostSpecification {
    
    public static Specification<JobPost> withFilters(
            String searchQuery,
            String location,
            Integer jobType,
            Integer experience,
            List<Integer> skillIds,
            String active
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("jobDescription")), searchPattern)
                ));
            }
            
            if (location != null && !location.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("jobLocation")),
                    "%" + location.toLowerCase() + "%"
                ));
            }
            
            if (jobType != null) {
                predicates.add(criteriaBuilder.equal(root.get("jobType").get("id"), jobType));
            }
            
            if (experience != null) {
                predicates.add(criteriaBuilder.equal(root.get("experience").get("id"), experience));
            }

            if (skillIds != null && !skillIds.isEmpty()) {
                predicates.add(root.join("jobPostSkillSets").get("skill").get("id").in(skillIds));
            }
            
            if (active != null && !"all".equalsIgnoreCase(active)) {
                boolean isActive = !"false".equalsIgnoreCase(active);
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 