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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics/companies")
@Tag(
    name = "Company Statistics",
    description = """
        Endpoints for retrieving company statistics and analytics.
        
        ## Authentication
        All endpoints require a valid JWT token in the Authorization header:
        ```
        Authorization: Bearer <your_jwt_token>
        ```
        
        ## Error Responses
        All endpoints return standardized error responses in the following format:
        ```json
        {
            "success": false,
            "message": "Error description",
            "data": null
        }
        ```
        """
)
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CompanyStatisticsController {

    private final CompanyStatisticsService companyStatisticsService;

    @GetMapping("/top-with-vacancies")
    @Operation(
        summary = "Get top companies with their vacancies",
        description = """
            Returns a list of top companies along with their most recent job posts.
            Companies are ranked based on their total number of vacancies.
            
            ## Response Format
            ```json
            {
                "success": true,
                "message": "Top companies retrieved successfully",
                "data": [
                    {
                        "companyId": 1,
                        "companyName": "Tech Solutions",
                        "totalVacancies": 10,
                        "recentPosts": [
                            {
                                "id": 1,
                                "title": "Senior Developer",
                                "createdDateTime": "2024-03-20T10:00:00"
                            }
                        ]
                    }
                ]
            }
            ```
            
            ## Notes
            - Companies are sorted by total number of vacancies in descending order
            - Only active job posts are counted
            - Recent posts are limited to the 5 most recent ones
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top companies",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CompanyWithVacanciesDTO.class)),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "Top companies retrieved successfully",
                            "data": [
                                {
                                    "companyId": 1,
                                    "companyName": "Tech Solutions",
                                    "totalVacancies": 10,
                                    "recentPosts": [
                                        {
                                            "id": 1,
                                            "title": "Senior Developer",
                                            "createdDateTime": "2024-03-20T10:00:00"
                                        }
                                    ]
                                }
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                            "success": false,
                            "message": "Unauthorized - Invalid or missing token",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<List<CompanyWithVacanciesDTO>> getTopCompaniesWithVacancies() {
        return ResponseEntity.ok(companyStatisticsService.getTopCompaniesWithVacancies());
    }
} 