package devtitans.antoshchuk.devfusion2025backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@Schema(description = "Parameters for filtering and sorting job posts")
public class JobPostFilterRequestDTO {
    @Schema(description = "Search query (searches in title and description)", example = "Java Developer")
    private String searchQuery;

    @Schema(description = "Job location", example = "London")
    private String location;

    @Schema(description = "Job type ID", example = "1")
    private Integer jobType;

    @Schema(description = "Experience ID (from experience table)", example = "3")
    private Integer experience;

    @Schema(description = "Skill IDs to filter by", example = "[1,2,3]")
    private List<Integer> skillIds;

    @Schema(description = "Field to sort by", example = "createdDateTime", defaultValue = "createdDateTime")
    private String sortBy = "createdDateTime";

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC", defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "Page number (starts from 0)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Schema(description = "Number of items per page", example = "6", defaultValue = "6")
    private Integer size = 6;

    @Schema(description = "Active status filter: 'true' (default, only active), 'false' (only inactive), 'all' (all job posts)", example = "true", defaultValue = "true")
    private String active = "true";
} 