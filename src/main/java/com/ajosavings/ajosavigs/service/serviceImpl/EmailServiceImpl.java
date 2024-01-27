package com.ajosavings.ajosavigs.service.serviceImpl;

import com.ajosavings.ajosavigs.exception.UserNotFoundException;
import com.ajosavings.ajosavigs.models.PasswordToken;
import com.ajosavings.ajosavigs.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public static final String FORGOT_PASSWORD_SUBJECT = "Forgot Password";
    public static final String HTML_CONTEXT = "<p>Dear user, you have requested to reset your password.</p>\n" +
            "\n" +
            "<!-- Display the verification token within the email content -->\n" +
            "<p>Please click <a th:href=\"'https://linkedin.com' + ${passwordToken}\">here</a> to reset your password</p>\n" +
            "\n" +
            "\n" +
            "<p>If you did not request for this, please ignore this email.</p>\n";


    public void sendForgotPasswordEmail(String username, PasswordToken passwordToken) throws MessagingException {
        if (username == null) {
            // Throw a more descriptive exception for user not found
            throw new UserNotFoundException("Username cannot be null when sending forgot password email.");
        }
        Context context = new Context();
        context.setVariable("passwordToken", passwordToken.getToken());
//        String htmlContent = templateEngine.process("template/PasswordToken", context);
        sendHTMLEmail(username, FORGOT_PASSWORD_SUBJECT, HTML_CONTEXT);
    }
    @Override
    public void sendEmail(String toEmail, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);

        javaMailSender.send(mailMessage);
    }
    @Override
    public void sendHTMLEmail(String toEmail, String subject, String htmlContent) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }
}
