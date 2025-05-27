package com.discount_backend.Discount_backend.service.sendGrid;

import com.discount_backend.Discount_backend.service.auth.AuthService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public void sendEmail(String toEmail, String subject, String body) throws IOException {
        try {
            Email from = new Email(fromEmail);
            logger.info("THE MUST SEND EMAIL FROM '{}'.", fromEmail);
            Email to = new Email(toEmail);
            logger.info("THE MUST SEND EMAIL to '{}'.", toEmail);
            Content content = new Content("text/plain", body);
            logger.info("THE CONTENT IS '{}'", content);
            Mail mail = new Mail(from, subject, to, content);
            logger.info("THE mail is '{}'", mail);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();

            try {
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sg.api(request);
                logger.info("THE STATUS CODE: '{}' ", response.getStatusCode());
                logger.info("THE BODY: '{}' ", response.getBody());
                logger.info("THE headers: '{}' ", response.getHeaders());
            } catch (IOException ex) {
                throw ex;
            }
        } catch (IOException ex) {
            throw ex;
        }
    }
}