package devtitans.antoshchuk.devfusion2025backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Parameters for filtering and sorting job posts")
public class JobPostFilterRequestDTO {
    @Schema(description = "Search query (searches in title and description)", example = "Java Developer")
    private String searchQuery;

    @Schema(description = "Job location", example = "London")
    private String location;

    @Schema(description = "Company ID", example = "1")
    private Integer companyId;

    @Schema(description = "Job type", example = "FULL_TIME")
    private String jobType;

    @Schema(description = "Experience level", example = "SENIOR")
    private String gradation;

    @Schema(description = "Job post active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Field to sort by", example = "createdDateTime", defaultValue = "createdDateTime")
    private String sortBy = "createdDateTime";

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC", defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "Page number (starts from 0)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "Number of items per page", example = "6", defaultValue = "6")
    private Integer size = 6;
} 