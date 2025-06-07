package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.SearchHistory;
import devtitans.antoshchuk.devfusion2025backend.models.TelegramUser;
import devtitans.antoshchuk.devfusion2025backend.repositories.SearchHistoryRepository;
import devtitans.antoshchuk.devfusion2025backend.repositories.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(JobNotificationService.class);
    
    private final TelegramBotService telegramBotService;
    private final TelegramUserRepository telegramUserRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    
    public void notifyUsersAboutNewJob(String jobTitle, String companyName, String jobUrl,
                                     String jobType, String jobGradation, List<String> tags,
                                     List<String> skills) {
        List<TelegramUser> allUsers = telegramUserRepository.findAll();
        
        for (TelegramUser user : allUsers) {
            if (isJobRelevantForUser(user, jobType, jobGradation, tags, skills)) {
                telegramBotService.sendJobNotification(
                    user.getTelegramChatId(),
                    jobTitle,
                    companyName,
                    jobUrl
                );
            }
        }
    }
    
    private boolean isJobRelevantForUser(TelegramUser user, String jobType, String jobGradation,
                                       List<String> tags, List<String> skills) {
        // Check user preferences
        if (user.getPreferredJobTypes() != null && !user.getPreferredJobTypes().contains(jobType)) {
            return false;
        }
        
        if (user.getPreferredJobGradation() != null && 
            !user.getPreferredJobGradation().equals(jobGradation)) {
            return false;
        }
        
        // Check for tag matches
        if (user.getPreferredTags() != null && tags != null) {
            boolean hasTagMatch = user.getPreferredTags().stream()
                .anyMatch(tag -> tags.contains(tag));
            if (!hasTagMatch) {
                return false;
            }
        }
        
        // Check for skill matches
        if (user.getPreferredSkills() != null && skills != null) {
            boolean hasSkillMatch = user.getPreferredSkills().stream()
                .anyMatch(skill -> skills.contains(skill));
            if (!hasSkillMatch) {
                return false;
            }
        }
        
        // Check search history
        List<SearchHistory> userSearches = searchHistoryRepository
            .findByUserIdOrderBySearchTimestampDesc(user.getUserId());
            
        if (!userSearches.isEmpty()) {
            SearchHistory latestSearch = userSearches.get(0);
            
            // Check if job type matches recent search
            if (latestSearch.getJobType() != null && 
                !latestSearch.getJobType().equals(jobType)) {
                return false;
            }
            
            // Check if job gradation matches recent search
            if (latestSearch.getJobGradation() != null && 
                !latestSearch.getJobGradation().equals(jobGradation)) {
                return false;
            }
            
            // Check for tag matches in recent search
            if (latestSearch.getTags() != null && tags != null) {
                boolean hasTagMatch = latestSearch.getTags().stream()
                    .anyMatch(tag -> tags.contains(tag));
                if (!hasTagMatch) {
                    return false;
                }
            }
            
            // Check for skill matches in recent search
            if (latestSearch.getSkills() != null && skills != null) {
                boolean hasSkillMatch = latestSearch.getSkills().stream()
                    .anyMatch(skill -> skills.contains(skill));
                if (!hasSkillMatch) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public void saveSearchHistory(Long userId, String searchQuery, List<String> tags,
                                List<String> skills, Integer jobType, String jobGradation) {
        log.info("[saveSearchHistory] userId={}, searchQuery={}, tags={}, skills={}, jobType={}, jobGradation={}",
                userId, searchQuery, tags, skills, jobType, jobGradation);
        try {
            SearchHistory searchHistory = new SearchHistory(
                userId, searchQuery, tags, skills, jobType, jobGradation
            );
            searchHistoryRepository.save(searchHistory);
            log.info("[saveSearchHistory] Successfully saved search history for userId={}", userId);
        } catch (Exception e) {
            log.error("[saveSearchHistory] Error saving search history for userId={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
} 