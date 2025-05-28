package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.CompanyWithVacanciesDTO;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics/companies")
@Tag(name = "Company Statistics", description = "Endpoints for retrieving company statistics and analytics")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CompanyStatisticsController {

    private final CompanyStatisticsService companyStatisticsService;

    @GetMapping("/top-with-vacancies")
    @Operation(
        summary = "Get top companies with their vacancies",
        description = "Returns a list of top companies along with their most recent job posts. " +
                     "Companies are ranked based on their total number of vacancies."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top companies",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CompanyWithVacanciesDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(hidden = true))
        )
    })
    public ResponseEntity<List<CompanyWithVacanciesDTO>> getTopCompaniesWithVacancies() {
        return ResponseEntity.ok(companyStatisticsService.getTopCompaniesWithVacancies());
    }
} 