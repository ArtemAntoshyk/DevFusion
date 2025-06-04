package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.JobViewHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobViewHistoryRepository extends MongoRepository<JobViewHistory, String> {
    List<JobViewHistory> findByUserId(Long userId);
    List<JobViewHistory> findByUserIdOrderByViewTimestampDesc(Long userId);
} 