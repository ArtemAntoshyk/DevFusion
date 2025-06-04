package devtitans.antoshchuk.devfusion2025backend.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "telegram_users")
public class TelegramUser {
    @Id
    private String id;
    
    private Long userId; // ID from main system
    private Long telegramChatId;
    private String phoneNumber;
    private String email;
    private boolean isActive;
    private List<String> preferredJobTypes;
    private List<String> preferredSkills;
    private List<String> preferredTags;
    private String preferredJobGradation;
    
    public TelegramUser(Long userId, Long telegramChatId, String phoneNumber, String email) {
        this.userId = userId;
        this.telegramChatId = telegramChatId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isActive = true;
    }
} 