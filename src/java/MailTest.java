/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailTest {
    public static void main(String[] args) {
        final String from = "aqilah060404@gmail.com";
        final String password = "nbsofzvrdriryeaj";
        final String to = "yourparentemail@gmail.com"; // 🔁 use your own Gmail for testing

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Testing Email");
            message.setText("This is a test email from Java using Gmail SMTP.");

            Transport.send(message);
            System.out.println("✅ Email sent successfully.");
        } catch (Exception e) {
            System.out.println("❌ Failed to send email.");
            e.printStackTrace();
        }
    }
}
