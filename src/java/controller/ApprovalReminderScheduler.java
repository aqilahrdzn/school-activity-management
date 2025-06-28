/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
// 1. Add a new column to parent_approval table
// ALTER TABLE parent_approval ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP;
// ALTER TABLE parent_approval ADD COLUMN disqualified BOOLEAN DEFAULT FALSE;

// 2. Create a scheduler using a servlet or external cron to check and update disqualified status



import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

@WebServlet(name = "ApprovalReminderScheduler", loadOnStartup = 1)
public class ApprovalReminderScheduler extends HttpServlet {

    @Override
    public void init() throws ServletException {
        Timer timer = new Timer();

        // Run every 24 hours (e.g. every midnight)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkPendingApprovals();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 60 * 60 * 24);
    }

    private void checkPendingApprovals() throws SQLException {
        String fetchQuery = "SELECT pa.id, pa.parent_id, pa.event_id, pa.created_at, p.email, s.student_name, e.title " +
                "FROM parent_approval pa " +
                "JOIN parent p ON pa.parent_id = p.id " +
                "JOIN student s ON s.parent_id = p.id " +
                "JOIN events e ON pa.event_id = e.id " +
                "WHERE pa.status IS NULL AND pa.disqualified = FALSE";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(fetchQuery);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int approvalId = rs.getInt("id");
                String email = rs.getString("email");
                String studentName = rs.getString("student_name");
                String eventTitle = rs.getString("title");
                Timestamp createdAt = rs.getTimestamp("created_at");

                long daysElapsed = (System.currentTimeMillis() - createdAt.getTime()) / (1000 * 60 * 60 * 24);

                if (daysElapsed == 3) {
                    sendReminderEmail(email, studentName, eventTitle);
                } else if (daysElapsed > 3) {
                    markAsDisqualified(conn, approvalId);
                }
            }
        }
    }

    private void markAsDisqualified(Connection conn, int approvalId) throws SQLException {
        String update = "UPDATE parent_approval SET status = 'Disqualified', disqualified = TRUE WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setInt(1, approvalId);
            ps.executeUpdate();
        }
    }

    private void sendReminderEmail(String toEmail, String studentName, String eventTitle) {
        final String senderEmail = "aqilah060404@gmail.com";
        final String senderPassword = "nbsofzvrdriryeaj";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[Reminder] Approval Needed for Event: " + eventTitle);

            message.setText("Assalamualaikum dan Salam Sejahtera,\n\n" +
                    "Ini adalah peringatan untuk melengkapkan kelulusan bagi penyertaan anak/jagaan anda, " + studentName +
                    " dalam acara \"" + eventTitle + "\".\n\nSila buat keputusan selewat-lewatnya hari ini untuk mengelakkan status penyertaan terbatal.\n\nTerima kasih.");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
