/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Required libraries:
// - iText PDF library (e.g. itextpdf-5.5.13.2.jar)

package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import util.DBConfig;
import util.MailUtil;

@WebServlet("/GenerateSuratServlet")
public class GenerateSuratServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int eventId = (int) request.getAttribute("eventId");

        String query = "SELECT s.student_name, s.ic_number, s.class, " +
        "p.name AS parent_name, p.ic_number AS parent_ic, p.email, " +
        "e.title, e.description, e.start_time " +
        "FROM event_participants ep " +
        "JOIN student s ON ep.student_ic = s.ic_number " +
        "JOIN parent p ON s.parent_id = p.id " +
        "JOIN events e ON ep.event_id = e.id " +
        "WHERE ep.event_id = ?";


        String uploadDirPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String studentName = rs.getString("student_name");
                String studentIC = rs.getString("student_ic");
                String className = rs.getString("class");
                String parentName = rs.getString("parent_name");
                String parentIC = rs.getString("parent_ic");
                String parentEmail = rs.getString("email");
                String title = rs.getString("title");
                String description = rs.getString("description");
                Timestamp startTime = rs.getTimestamp("start_time");
                

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
                String dateStr = sdf.format(startTime);

                // Generate PDF
                String fileName = "surat_" + studentIC + ".pdf";
                String filePath = uploadDirPath + File.separator + fileName;
                createSuratPDF(filePath, parentName, parentIC, studentName, className, title, dateStr);

                // Optionally save path to DB (e.g. in event_participants table)
                PreparedStatement updateStmt = con.prepareStatement("UPDATE event_participants SET surat_path = ? WHERE student_ic = ? AND event_id = ?");
                updateStmt.setString(1, "uploads/" + fileName);
                updateStmt.setString(2, studentIC);
                updateStmt.setInt(3, eventId);
                updateStmt.executeUpdate();

                // Send email notification to parent (no attachment)
                String fromEmail = "your_email@gmail.com";
                String emailPassword = "your_16_digit_app_password";
                String subject = "Notis Penyertaan Aktiviti Anak Anda";
                String message = "Tuan/Puan " + parentName + ",\n\n"
                        + "Surat pengesahan untuk penyertaan anak anda dalam aktiviti '" + title + "' telah disediakan."
                        + "\nSila log masuk ke sistem untuk membaca surat tersebut.\n\nSekian, terima kasih.";

                MailUtil.sendMail(fromEmail, emailPassword, parentEmail, subject, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/teacher/sendApproval.jsp?eventId=" + eventId + "&error=1");
            return;
        }

        response.sendRedirect("/teacher/sendApproval.jsp?eventId=" + eventId + "&success=1");
    }

    private void createSuratPDF(String filePath, String parentName, String parentIC, String studentName,
                                 String className, String activity, String dateStr) throws Exception {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12);

        document.add(new Paragraph("KEBENARAN IBU/BAPA PENJAGA", fontTitle));
        document.add(new Paragraph("\nSaya " + parentName + " No. Kad Pengenalan: " + parentIC
                + " adalah ibu/bapa kepada " + studentName + " dari kelas " + className + ","));

        document.add(new Paragraph("\ndengan ini membenarkan anak/jagaan saya menyertai aktiviti berikut:", fontNormal));
        document.add(new Paragraph("\nAktiviti: " + activity, fontNormal));
        document.add(new Paragraph("\nTarikh: " + dateStr, fontNormal));
        

        document.add(new Paragraph("\n\nSaya faham bahawa pihak sekolah akan memberi taklimat keselamatan dan akan bertanggungjawab sepanjang aktiviti berlangsung.", fontNormal));
        document.add(new Paragraph("\nSaya bersetuju memberikan kebenaran ini tanpa paksaan.", fontNormal));

        document.add(new Paragraph("\n\nSekian, terima kasih.", fontNormal));
        document.add(new Paragraph("\n\nYang benar,\n\n____________________\n(" + parentName + ")", fontNormal));

        document.close();
    }
}

