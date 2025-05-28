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
            Integer companyId,
            String jobType,
            String gradation,
            Boolean isActive
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
            
            if (companyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), companyId));
            }
            
            if (jobType != null && !jobType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("jobType").get("name"), jobType));
            }
            
            if (gradation != null && !gradation.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("jobGradation").get("name"), gradation));
            }
            
            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 