package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.chat.ChatMessage;
import devtitans.antoshchuk.devfusion2025backend.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(Integer seekerId, Integer companyId, String senderType, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSeekerId(seekerId);
        chatMessage.setCompanyId(companyId);
        chatMessage.setSenderType(senderType);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(new Date());
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(Integer seekerId, Integer companyId) {
        return chatMessageRepository.findBySeekerIdAndCompanyIdOrderByTimestampAsc(seekerId, companyId);
    }

    public List<ChatPreview> getChatsForSeeker(Integer seekerId) {
        List<ChatMessage> messages = chatMessageRepository.findBySeekerIdOrderByTimestampDesc(seekerId);
        // Группируем по companyId, берем последнее сообщение
        Map<Integer, ChatMessage> lastMessages = messages.stream()
                .collect(Collectors.toMap(ChatMessage::getCompanyId, m -> m, (m1, m2) -> m1));
        return lastMessages.values().stream()
                .map(ChatPreview::fromMessage)
                .collect(Collectors.toList());
    }

    public List<ChatPreview> getChatsForCompany(Integer companyId) {
        List<ChatMessage> messages = chatMessageRepository.findByCompanyIdOrderByTimestampDesc(companyId);
        // Группируем по seekerId, берем последнее сообщение
        Map<Integer, ChatMessage> lastMessages = messages.stream()
                .collect(Collectors.toMap(ChatMessage::getSeekerId, m -> m, (m1, m2) -> m1));
        return lastMessages.values().stream()
                .map(ChatPreview::fromMessage)
                .collect(Collectors.toList());
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ChatPreview {
        private Integer seekerId;
        private Integer companyId;
        private String lastMessage;
        private String senderType;
        private java.util.Date timestamp;

        public static ChatPreview fromMessage(ChatMessage msg) {
            return new ChatPreview(msg.getSeekerId(), msg.getCompanyId(), msg.getMessage(), msg.getSenderType(), msg.getTimestamp());
        }
    }
} 