package controller;

import dao.NotificationDAO;
import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Paths;
import java.sql.*;

@WebServlet("/SubmitApprovalServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class SubmitApprovalServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "uploads/resit";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String parentIdStr = request.getParameter("parent_id");
        String eventIdStr = request.getParameter("event_id");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason") != null ? request.getParameter("reason") : "";
        String eventCategory = request.getParameter("event_category");

        // Validate and parse IDs safely
        if (parentIdStr == null || eventIdStr == null) {
            response.getWriter().println("Missing parent or event ID.");
            return;
        }

        int parentId;
        int eventId;
        try {
            parentId = Integer.parseInt(parentIdStr);
            eventId = Integer.parseInt(eventIdStr);
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid ID format.");
            return;
        }

        String resitFilePath = null;
        ByteArrayOutputStream baos = null;

        if ("payment".equalsIgnoreCase(eventCategory)) {
            try {
                Part filePart = request.getPart("resit");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    File file = new File(uploadPath + File.separator + fileName);
                    InputStream originalStream = filePart.getInputStream();

                    baos = new ByteArrayOutputStream();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = originalStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            baos.write(buffer, 0, bytesRead);
                        }
                    }
                    originalStream.close();
                    resitFilePath = UPLOAD_DIRECTORY + "/" + fileName;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try (Connection con = DBConfig.getConnection()) {

            boolean exists = false;
            String checkSql = "SELECT COUNT(*) FROM parent_approval WHERE parent_id = ? AND event_id = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setInt(1, parentId);
                checkStmt.setInt(2, eventId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            String sql;
            if (exists) {
                sql = "UPDATE parent_approval SET status = ?, reason = ?, approved_at = NOW(), resit_file = ?, resit_blob = ? "
                        + "WHERE parent_id = ? AND event_id = ?";
            } else {
                sql = "INSERT INTO parent_approval (status, reason, approved_at, resit_file, resit_blob, parent_id, event_id) "
                        + "VALUES (?, ?, NOW(), ?, ?, ?, ?)";
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, reason);
                ps.setString(3, resitFilePath);

                if (baos != null) {
                    InputStream blobStream = new ByteArrayInputStream(baos.toByteArray());
                    ps.setBlob(4, blobStream);
                } else {
                    ps.setNull(4, Types.BLOB);
                }

                ps.setInt(5, parentId);
                ps.setInt(6, eventId);

                ps.executeUpdate();
            }

            // ✅ Get the creator's email from events
            String teacherEmail = null;
            String getEmailSql = "SELECT created_by FROM events WHERE id = ?";
            try (PreparedStatement stmt = con.prepareStatement(getEmailSql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        teacherEmail = rs.getString("created_by");
                    }
                }
            }

// ✅ If email was found, fetch teacher ID and send notification
            if (teacherEmail != null) {
                String getTeacherIdSql = "SELECT id FROM teachers WHERE email = ?";
                try (PreparedStatement stmt = con.prepareStatement(getTeacherIdSql)) {
                    stmt.setString(1, teacherEmail);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int teacherId = rs.getInt("id");
                            NotificationDAO dao = new NotificationDAO();
                            dao.insertNotifications(teacherId, "teacher", "New approval by parent, review it.", eventId);

                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/parent/studentEvent.jsp?success=true");
    }
}
