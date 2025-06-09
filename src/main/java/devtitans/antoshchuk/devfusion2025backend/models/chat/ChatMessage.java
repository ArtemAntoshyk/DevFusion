package devtitans.antoshchuk.devfusion2025backend.models.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private Integer seekerId;
    private Integer companyId;
    private String senderType; // "SEEKER" или "COMPANY"
    private String message;
    private Date timestamp;
} 