package devtitans.antoshchuk.devfusion2025backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostSkillDTO {
    private Integer id;
    private String name;
    private Integer level;
} 