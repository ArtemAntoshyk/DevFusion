package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.JobViewHistory;
import devtitans.antoshchuk.devfusion2025backend.repositories.JobViewHistoryRepository;
import devtitans.antoshchuk.devfusion2025backend.dto.response.JobPostResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.response.RecommendationResponseDTO;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserAccountRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableAsync
@EnableScheduling
@Service
@RequiredArgsConstructor
public class JobViewHistoryService {

    @Autowired
    private JavaMailSender mailSender;
    private final JobViewHistoryRepository jobViewHistoryRepository;
    private final UserAccountRepository userAccountRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public void saveJobView(Long userId, Long jobPostId, String jobTitle, String companyName) {
        JobViewHistory history = new JobViewHistory(userId, jobPostId, jobTitle, companyName);
        jobViewHistoryRepository.save(history);
        scheduleRecommendationEmail(userId, jobPostId);
    }

    public List<JobViewHistory> getUserViewHistory(Long userId) {
        return jobViewHistoryRepository.findByUserIdOrderByViewTimestampDesc(userId);
    }

    private void scheduleRecommendationEmail(Long userId, Long jobPostId) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> sendRecommendationEmail(userId, jobPostId), 1, TimeUnit.MINUTES);
    }

    @Async
    public void sendRecommendationEmail(Long userId, Long jobPostId) {
        UserAccount user = userAccountRepository.findById(userId.intValue()).orElse(null);
        if (user == null || user.getEmail() == null) return;
        String email = user.getEmail();
        // Получаем рекомендации через API
        String url = "http://localhost:8082/api/v1/recommendations/for-job/" + jobPostId;
        RecommendationResponseDTO recs = restTemplate.getForObject(url, RecommendationResponseDTO.class);
        if (recs == null || recs.getJobs() == null || recs.getJobs().isEmpty()) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Recommended jobs for you");
            StringBuilder html = new StringBuilder();
            html.append("<h2>We picked some jobs for you!</h2>");
            for (JobPostResponseDTO job : recs.getJobs()) {
                html.append("<div style='border:1px solid #eee;padding:12px;margin-bottom:16px;border-radius:8px;'>");
                html.append("<h3>" + job.getTitle() + " at " + (job.getCompany()!=null?job.getCompany().getName():"") + "</h3>");
                html.append("<p><b>Location:</b> " + job.getLocation() + "</p>");
                html.append("<p><b>Salary:</b> " + job.getSalaryRange() + "</p>");
                html.append("<p>" + job.getDescription() + "</p>");
                html.append("<a href='https://your-frontend.com/job/" + job.getId() + "' style='color:#fff;background:#007bff;padding:8px 16px;border-radius:4px;text-decoration:none;'>View job</a>");
                html.append("</div>");
            }
            helper.setText(html.toString(), true);
            helper.setFrom("artemantoshyk4@ukr.net");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 