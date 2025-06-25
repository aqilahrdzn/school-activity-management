package controller;

import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Paths;
import java.sql.*;

@WebServlet("/SubmitApprovalServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,    // 1MB
                 maxFileSize = 5 * 1024 * 1024,      // 5MB
                 maxRequestSize = 10 * 1024 * 1024)  // 10MB
public class SubmitApprovalServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "uploads/resit"; // Relative path inside webapp

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int parentId = Integer.parseInt(request.getParameter("parent_id"));
        int eventId = Integer.parseInt(request.getParameter("event_id"));
        String status = request.getParameter("status");
        String reason = request.getParameter("reason") != null ? request.getParameter("reason") : "";

        // Prepare file path (only for payment category events)
        String resitFilePath = null;

        // Get the file only if uploaded (for payment category)
        Part filePart = request.getPart("resit"); // file input name
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

            // Create folder if not exists
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file
            File file = new File(uploadPath + File.separator + fileName);
            try (InputStream fileContent = filePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                resitFilePath = UPLOAD_DIRECTORY + "/" + fileName;
            }
        }

        // Save approval info into DB (include resit file if needed)
        try (Connection con = DBConfig.getConnection()) {
            String sql = "INSERT INTO parent_approval (parent_id, event_id, status, reason, approved_at, resit_file) VALUES (?, ?, ?, ?, NOW(), ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, parentId);
            ps.setInt(2, eventId);
            ps.setString(3, status);
            ps.setString(4, reason);
            ps.setString(5, resitFilePath); // NULL if not uploaded

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("DB Error: " + e.getMessage());
            return;
        }

        response.sendRedirect("parent/studentEvent.jsp?success=true");
    }
}
