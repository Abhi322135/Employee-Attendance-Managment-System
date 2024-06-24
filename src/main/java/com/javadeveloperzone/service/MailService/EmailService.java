package com.javadeveloperzone.service.MailService;

import com.javadeveloperzone.constant.BooleanFlag;
import com.javadeveloperzone.constant.MailMessages;
import com.javadeveloperzone.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(String subject, String text, String... to) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MailMessages.UTF);


        try {
            helper.setText(text, BooleanFlag.TRUE);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(MailMessages.SENDER_EMAIL);
        } catch (MessagingException e) {
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,e.getMessage());
        }

        mailSender.send(mimeMessage);
       }
    }

