package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender; // ספרינג שולח מיילים דרכו

    // פונקציה לשליחת מייל — מקבלת כתובת, נושא ותוכן
    public void sendEmail(String toEmail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);              // למי שולחים
        message.setSubject(subject);         // נושא המייל
        message.setText(body);               // תוכן המייל
        message.setFrom("studysharegit@gmail.com"); // המייל השולח

        mailSender.send(message);            // שליחה בפועל
        System.out.println("📨 Mail sent to: " + toEmail);
    }
}
