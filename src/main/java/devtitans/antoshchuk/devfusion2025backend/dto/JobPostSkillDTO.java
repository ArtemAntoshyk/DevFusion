package devtitans.antoshchuk.devfusion2025backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skill for job post with level")
public class JobPostSkillDTO {
    @Schema(description = "Skill ID", example = "1")
    private Integer id;
    @Schema(description = "Skill name (optional, for response only)", example = "Java")
    private String name;
    @Schema(description = "Skill level (1 - Junior, 5 - Expert)", example = "3")
    private Integer level;
} 