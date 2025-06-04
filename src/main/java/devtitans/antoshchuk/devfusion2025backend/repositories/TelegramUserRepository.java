package devtitans.antoshchuk.devfusion2025backend.repositories;

import devtitans.antoshchuk.devfusion2025backend.models.TelegramUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserRepository extends MongoRepository<TelegramUser, String> {
    Optional<TelegramUser> findByTelegramChatId(Long telegramChatId);
    Optional<TelegramUser> findByUserId(Long userId);
    Optional<TelegramUser> findByPhoneNumber(String phoneNumber);
    Optional<TelegramUser> findByEmail(String email);
} 