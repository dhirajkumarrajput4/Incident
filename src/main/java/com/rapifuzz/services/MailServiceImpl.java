package com.rapifuzz.services;

import com.rapifuzz.request_response.Mail;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;


@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(Mail mail) {
        //mail delivered from this mail id
        mail.setMailFrom("mydesktop2662@gmail.com");

        String to = mail.getMailTo();
        String cc = mail.getMailCc();
        String bcc = mail.getMailBcc();
        String subject = mail.getMailSubject();
        String sender = mail.getMailFrom();
        String bodyHtml = mail.getMailContent();
        List<String> fileAttachments=null;

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // Add subject, from and to lines.
            message.setSubject(subject, "UTF-8");
            message.setFrom(new InternetAddress(sender));
            if (StringUtils.hasLength(to)) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            }
            if (StringUtils.hasLength(cc)) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
            }
            if (StringUtils.hasLength(bcc)) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
            }

            // Create a multipart/alternative child container.

            MimeMultipart msg_body = new MimeMultipart("alternative");

            // Define the HTML part.
            if (bodyHtml != null) {
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(bodyHtml, "text/html; charset=UTF-8");
                msg_body.addBodyPart(htmlPart);
            }

            // Add file attachments
            if (fileAttachments != null) {
                for (String filePath : fileAttachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(new File(filePath));
                    msg_body.addBodyPart(attachmentPart);
                }
            }
            // Add the multipart/alternative container to the message.
            message.setContent(msg_body);

            // Send the message.
            javaMailSender.send(message);

        } catch (Exception ex) {
            throw new RuntimeException(String.format("unable to send mail to %s cc %s bcc %s subject %s error %s", to, cc, bcc, subject, ex.getMessage()), ex);
        }

    }
}
