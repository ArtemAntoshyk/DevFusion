package devtitans.antoshchuk.devfusion2025backend.config;

import devtitans.antoshchuk.devfusion2025backend.services.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfig {
    
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotService telegramBotService) throws TelegramApiException {
        log.info("Initializing Telegram Bot API...");
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            log.info("Registering bot with username: {}", telegramBotService.getBotUsername());
            telegramBotsApi.registerBot(telegramBotService);
            log.info("Bot successfully registered!");
            return telegramBotsApi;
        } catch (TelegramApiException e) {
            log.error("Failed to initialize Telegram Bot API: {}", e.getMessage(), e);
            throw e;
        }
    }
} 