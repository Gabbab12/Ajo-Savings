package com.ajosavings.ajosavigs.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String content);

    void sendHTMLEmail(String toEmail, String subject, String htmlContent) throws MessagingException;
}
