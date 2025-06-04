package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.JobViewHistory;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobViewHistoryService {
    
    private final JobViewHistoryRepository jobViewHistoryRepository;

    public void saveJobView(Long userId, Long jobPostId, String jobTitle, String companyName) {
        JobViewHistory history = new JobViewHistory(userId, jobPostId, jobTitle, companyName);
        jobViewHistoryRepository.save(history);
    }

    public List<JobViewHistory> getUserViewHistory(Long userId) {
        return jobViewHistoryRepository.findByUserIdOrderByViewTimestampDesc(userId);
    }
} 