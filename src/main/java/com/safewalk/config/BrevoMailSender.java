package com.safewalk.config;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BrevoMailSender implements JavaMailSender {

    private final String apiKey;
    private final String senderEmail;
    private final String senderName;
    private final JavaMailSender fallbackSender;
    private final RestTemplate restTemplate;

    public BrevoMailSender(String apiKey, String senderEmail, String senderName, JavaMailSender fallbackSender) {
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.fallbackSender = fallbackSender;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            sendViaBrevo(simpleMessage);
        } else if (fallbackSender != null) {
            log.info("Brevo API key not configured. Falling back to SMTP sender.");
            fallbackSender.send(simpleMessage);
        } else {
            throw new MailSendException("No mail sender configured (Brevo API key is missing and no SMTP fallback available).");
        }
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        for (SimpleMailMessage message : simpleMessages) {
            send(message);
        }
    }

    private void sendViaBrevo(SimpleMailMessage message) {
        String url = "https://api.brevo.com/v3/smtp/email";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            Map<String, Object> body = new HashMap<>();

            // Sender info
            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            body.put("sender", sender);

            // Recipients
            List<Map<String, String>> toList = new ArrayList<>();
            if (message.getTo() != null) {
                for (String toEmail : message.getTo()) {
                    Map<String, String> to = new HashMap<>();
                    to.put("email", toEmail);
                    toList.add(to);
                }
            }
            body.put("to", toList);

            // Subject and content
            body.put("subject", message.getSubject());
            body.put("textContent", message.getText());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully via Brevo to: {}", (Object) message.getTo());
            } else {
                log.error("Failed to send email via Brevo. Status: {}, Response: {}", response.getStatusCode(), response.getBody());
                throw new MailSendException("Failed to send email via Brevo. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while sending email via Brevo: {}", e.getMessage(), e);
            throw new MailSendException("Failed to send email via Brevo", e);
        }
    }

    // Standard JavaMailSender interface methods we do not use but need to implement
    @Override
    public MimeMessage createMimeMessage() {
        if (fallbackSender != null) {
            return fallbackSender.createMimeMessage();
        }
        throw new UnsupportedOperationException("MimeMessage creation is not supported when SMTP fallback is unavailable.");
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        if (fallbackSender != null) {
            return fallbackSender.createMimeMessage(contentStream);
        }
        throw new UnsupportedOperationException("MimeMessage creation is not supported when SMTP fallback is unavailable.");
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        if (fallbackSender != null) {
            fallbackSender.send(mimeMessage);
        } else {
            throw new UnsupportedOperationException("MimeMessage sending is not supported when SMTP fallback is unavailable.");
        }
    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        if (fallbackSender != null) {
            fallbackSender.send(mimeMessages);
        } else {
            throw new UnsupportedOperationException("MimeMessage sending is not supported when SMTP fallback is unavailable.");
        }
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        if (fallbackSender != null) {
            fallbackSender.send(mimeMessagePreparator);
        } else {
            throw new UnsupportedOperationException("MimeMessage sending is not supported when SMTP fallback is unavailable.");
        }
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        if (fallbackSender != null) {
            fallbackSender.send(mimeMessagePreparators);
        } else {
            throw new UnsupportedOperationException("MimeMessage sending is not supported when SMTP fallback is unavailable.");
        }
    }
}
