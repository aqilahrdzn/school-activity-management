/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javax.mail.*;          // Changed from jakarta.mail
import javax.mail.internet.*; // Changed from jakarta.mail.internet
import java.util.Properties;

public class EmailUtil {

    // IMPORTANT: These should be loaded from secure configuration (e.g., environment variables, JNDI)
    // DO NOT hardcode sensitive credentials in production code.
    private static final String SMTP_HOST = "smtp.gmail.com"; // e.g., "smtp.gmail.com" for Gmail
    private static final String SMTP_PORT = "587";            // e.g., "587" for TLS, "465" for SSL
    private static final String SENDER_EMAIL = "aqilah060404@gmail.com"; // The email address that sends the emails
    private static final String SENDER_PASSWORD = "nbsofzvrdriryeaj"; // Use an App Password for Gmail 2FA

    /**
     * Sends a registration email to the newly registered teacher.
     *
     * @param recipientEmail The email address of the teacher.
     * @param teacherName The name of the teacher.
     * @param teacherPassword The password assigned to the teacher.
     */
    public static void sendRegistrationEmail(String recipientEmail, String teacherName, String teacherPassword) {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS for secure connection
        // For debugging: properties.put("mail.debug", "true");

        // Create a Session object with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            // Create a new MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the sender address
            message.setFrom(new InternetAddress(SENDER_EMAIL, "School Administration")); // Add a friendly name

            // Set the recipient address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set the email subject
            message.setSubject("Password for School Activity Management System");

            // Create the email content
            String emailContent = "Dear " + teacherName + ",\n\n"
                                + "Your teacher account has been successfully registered by the school clerk at SK Kerayong Jaya .\n\n"
                                + "Here are your login details:\n"
                                + "Email: " + recipientEmail + "\n"
                                + "Password: " + teacherPassword + "\n\n"
                                + "For security reasons, we highly recommend that you log in as soon as possible and change your password.\n\n"
            
                                + "If you have any questions, please do not hesitate to contact us.\n\n"
                                + "Sincerely,\n"
                                + "The School Administration Team";

            message.setText(emailContent);

            // Send the message
            Transport.send(message);

            System.out.println("Registration email successfully sent to: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send registration email to " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
