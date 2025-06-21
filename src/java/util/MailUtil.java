package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailUtil {

    public static void sendMail(final String from, final String password, String to, String subject, String msgText) {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(msgText);

            Transport.send(message);
            System.out.println("✅ Email sent to: " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email to: " + to);
            e.printStackTrace(); // Log full error
        }
    }
}
