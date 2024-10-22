package com.example.ecm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailNotificationService {
    private final JavaMailSender mailSender;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void notifyUserSignature(Long userId, String documentTitle){
        String subject = "Подпишите документ";
        String text = String.format("Вам пришел документ %s на подпись", documentTitle);
        String userEmail = userService.getUserById(userId).getEmail();

        send(userEmail, subject, text);
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
