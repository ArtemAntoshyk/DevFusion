package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.dto.response.CompanyBaseResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.services.CompanyService;
import devtitans.antoshchuk.devfusion2025backend.services.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job-post")
public class JobPostController {
    private JobPostService jobPostService;
    private CompanyService companyService;
    @Autowired
    public JobPostController(JobPostService jobPostService, CompanyService companyService) {
        this.jobPostService = jobPostService;
        this.companyService = companyService;
    }

    @GetMapping("/all-post-with-pagination/")
    public ResponseEntity<List<CompanyBaseResponseDTO>> allPostWithPagination() {
        return ResponseEntity.ok(companyService.getAllCompaniesBaseInfoDTOs());
    }


}
