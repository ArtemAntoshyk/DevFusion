package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.SearchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends MongoRepository<SearchHistory, String> {
    List<SearchHistory> findByUserId(Long userId);
    List<SearchHistory> findByUserIdOrderBySearchTimestampDesc(Long userId);
} 