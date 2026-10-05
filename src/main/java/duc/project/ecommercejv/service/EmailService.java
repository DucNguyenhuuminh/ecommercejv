package duc.project.ecommercejv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private String clientUrl;

    public void sendVerificationEmail(String to, String token) {
        String link = clientUrl + "/verify-email?token=" + token;
        send(to, "Verify your email", "Click into the link to verify email (expires after 24 hours):\n" + link);
    }

    public void sendResetPasswordEmail(String to, String token) {
        String link = clientUrl + "/reset-password?token=" + token;
        send(to, "Reset your password", "Click into this link to reset the password (expires after 15 minutes):\n" + link + "\n\nIf your are not required, please skip this email.");
    }

//    ==========HELPER==========
    private void send (String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
