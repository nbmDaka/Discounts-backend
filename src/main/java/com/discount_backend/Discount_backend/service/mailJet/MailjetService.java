package com.discount_backend.Discount_backend.service.mailJet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailjetService {

    @Value("${mailjet.api-key}")
    private String apiKey;

    @Value("${mailjet.secret-key}")
    private String secretKey;

    @Value("${mailjet.from.email}")
    private String fromEmail;

    @Value("${mailjet.from.name}")
    private String fromName;

    public void sendVerificationEmail(String toEmail, String subject, String htmlBody) {
        String url = "https://api.mailjet.com/v3.1/send";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = apiKey + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        Map<String, Object> message = new HashMap<>();
        message.put("From", Map.of("Email", fromEmail, "Name", fromName));
        message.put("To", new Map[]{Map.of("Email", toEmail)});
        message.put("Subject", subject);
        message.put("HTMLPart", htmlBody);

        Map<String, Object> body = Map.of("Messages", new Map[]{message});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send email via Mailjet: " + response.getBody());
        }
    }
}