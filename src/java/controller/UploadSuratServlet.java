package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import util.DBConfig;
import util.MailUtil;

@WebServlet("/UploadSuratServlet")
@MultipartConfig
public class UploadSuratServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int eventId = Integer.parseInt(request.getParameter("eventId"));
        Part filePart = request.getPart("approvalLetter");

        try (Connection con = DBConfig.getConnection()) {

            // Check if already uploaded
            String checkQuery = "SELECT surat_pengesahan FROM events WHERE id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, eventId);
            ResultSet rsCheck = checkStmt.executeQuery();

            if (rsCheck.next()) {
                String existingFile = rsCheck.getString("surat_pengesahan");
                if (existingFile != null && !existingFile.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/teacher/sendApproval.jsp?eventId=" + eventId + "&error=alreadyUploaded");
                    return;
                }
            }

            // Save file to uploads
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String savedFilePath = uploadPath + File.separator + fileName;
            filePart.write(savedFilePath); // Save to file system

            // Save to DB (both file path and blob)
            try (InputStream fileInputStream = new FileInputStream(savedFilePath)) {
                String updateSQL = "UPDATE events SET surat_pengesahan=?, surat_blob=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(updateSQL);
                ps.setString(1, "uploads/" + fileName);
                ps.setBlob(2, fileInputStream);
                ps.setInt(3, eventId);
                ps.executeUpdate();
            }

            // Send email to parents
            String emailQuery = "SELECT DISTINCT p.email "
                    + "FROM event_participants ep "
                    + "JOIN student s ON ep.student_ic = s.ic_number "
                    + "JOIN parent p ON s.parent_id = p.id "
                    + "WHERE ep.event_id = ?";
            PreparedStatement ps2 = con.prepareStatement(emailQuery);
            ps2.setInt(1, eventId);
            ResultSet rs = ps2.executeQuery();

            String fromEmail = "aqilah060404@gmail.com"; // ✅ change this
            String emailPassword = "nbsofzvrdriryeaj"; // ✅ 16-digit Gmail App Password

            while (rs.next()) {
                String parentEmail = rs.getString("email");
                String subject = "Surat Pengesahan for Event Approval";
                String message = "Dear Parent,\n\nA surat pengesahan has been uploaded for an event involving your child.\nPlease log in to the system and approve it.\n\nThank you.";

                MailUtil.sendMail(fromEmail, emailPassword, parentEmail, subject, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/teacher/sendApproval.jsp?eventId=" + eventId + "&error=emailFailed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/teacher/sendApproval.jsp?eventId=" + eventId + "&success=1");
    }
}


