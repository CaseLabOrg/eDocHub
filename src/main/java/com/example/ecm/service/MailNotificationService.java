package com.example.ecm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки уведомлений по электронной почте.
 */
@Service
@RequiredArgsConstructor
public class MailNotificationService {
    private final JavaMailSender mailSender;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Уведомляет пользователя о том, что ему пришел документ на подпись.
     *
     * @param userId        идентификатор пользователя, которому отправляется уведомление.
     * @param documentTitle название документа, который нужно подписать.
     */
    @Async
    public void notifyUserSignature(Long userId, String documentTitle){
        String subject = "Подпишите документ";
        String text = String.format("Вам пришел документ \"%s\" на подпись", documentTitle);
        String userEmail = userService.getUserById(userId, true).getEmail();

        send(userEmail, subject, text);
    }

    /**
     * Отправляет письмо с заданной темой и текстом на указанный адрес.
     *
     * @param to      адрес электронной почты получателя.
     * @param subject тема письма.
     * @param text    текст письма.
     */
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
