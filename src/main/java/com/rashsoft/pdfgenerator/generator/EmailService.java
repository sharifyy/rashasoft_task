package com.rashsoft.pdfgenerator.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final JavaMailSender emailSender;
    @Value(value = "${admin.email.address}")
    private String adminEmail;

    public void sendHtmlMessage(String subject, String body)  {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            message.setText(body, "utf-8", "plain");
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(adminEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            emailSender.send(message);
            log.info("email sent");

        }catch (MessagingException messagingException){
            log.error("sending email failed: "+messagingException.getMessage());
        }
    }
}
