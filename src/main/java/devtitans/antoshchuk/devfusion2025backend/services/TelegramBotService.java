package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.TelegramUser;
import devtitans.antoshchuk.devfusion2025backend.repositories.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    private final TelegramUserRepository telegramUserRepository;
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            
            if (messageText.startsWith("/start")) {
                handleStartCommand(chatId);
            } else if (messageText.startsWith("/register")) {
                handleRegistration(chatId, messageText);
            }
        }
    }
    
    private void handleStartCommand(Long chatId) {
        String message = "Welcome to Job Notifications Bot! 🚀\n\n" +
                "To start receiving job notifications, please register using the command:\n" +
                "/register <phone_number> <email>\n\n" +
                "Example: /register +380501234567 user@example.com";
        sendMessage(chatId, message);
    }
    
    private void handleRegistration(Long chatId, String messageText) {
        String[] parts = messageText.split(" ");
        if (parts.length != 3) {
            sendMessage(chatId, "Invalid format. Please use: /register <phone_number> <email>");
            return;
        }
        
        String phoneNumber = parts[1];
        String email = parts[2];
        
        // Check if user already exists
        if (telegramUserRepository.findByPhoneNumber(phoneNumber).isPresent() ||
            telegramUserRepository.findByEmail(email).isPresent()) {
            sendMessage(chatId, "This phone number or email is already registered.");
            return;
        }
        
        // Create new Telegram user
        TelegramUser telegramUser = new TelegramUser(null, chatId, phoneNumber, email);
        telegramUserRepository.save(telegramUser);
        
        sendMessage(chatId, "Registration successful! You will receive job notifications soon.");
    }
    
    public void sendJobNotification(Long chatId, String jobTitle, String companyName, String jobUrl) {
        String message = String.format("🔥 Нова вакансія для вас:\n\n%s в компанії %s\n\nПереглянути: %s",
                jobTitle, companyName, jobUrl);
        sendMessage(chatId, message);
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            // Log error
            e.printStackTrace();
        }
    }
} 