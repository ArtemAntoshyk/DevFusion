package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findBySeekerIdAndCompanyIdOrderByTimestampAsc(Integer seekerId, Integer companyId);
    List<ChatMessage> findBySeekerIdOrderByTimestampDesc(Integer seekerId);
    List<ChatMessage> findByCompanyIdOrderByTimestampDesc(Integer companyId);
} 