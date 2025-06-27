package controller;

import java.io.*;
import java.sql.*;
import java.util.Collection;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import util.DBConfig;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
@WebServlet("/UploadEventFileServlet")
public class UploadEventFileServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        String eventIdParam = request.getParameter("eventId");
        int eventId = (eventIdParam != null && !eventIdParam.isEmpty()) ? Integer.parseInt(eventIdParam) : 0;
        String description = request.getParameter("description");

        String message = "";
        boolean uploadSuccess = true;
        boolean hasNewFiles = false;

        Collection<Part> fileParts = request.getParts();

        try (Connection conn = DBConfig.getConnection()) {

            // Step 1: Delete old files (optional cleanup logic)
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM event_uploads WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // Step 2: Save new files (if any)
            String insertSQL = "INSERT INTO event_uploads (event_id, file_name, file_path, file_type, description) VALUES (?, ?, ?, ?, ?)";

            for (Part part : fileParts) {
                if (part.getName().equals("eventFile") && part.getSize() > 0) {
                    hasNewFiles = true;
                    String fileName = extractFileName(part);
                    String fileType = part.getContentType();

                    if (!isAllowedFileType(fileType)) {
                        message += "Skipped '" + fileName + "': Invalid file type.<br>";
                        uploadSuccess = false;
                        continue;
                    }

                    String filePath = uploadPath + File.separator + fileName;
                    try (InputStream input = part.getInputStream();
                         FileOutputStream output = new FileOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        message += "Failed to save file to disk: " + fileName + "<br>";
                        uploadSuccess = false;
                        continue;
                    }

                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, eventId);
                        insertStmt.setString(2, fileName);
                        insertStmt.setString(3, UPLOAD_DIR + "/" + fileName);
                        insertStmt.setString(4, fileType);
                        insertStmt.setString(5, description);
                        insertStmt.executeUpdate();
                    } catch (SQLException e) {
                        message += "Failed to insert file '" + fileName + "' to DB: " + e.getMessage() + "<br>";
                        uploadSuccess = false;
                        continue;
                    }
                }
            }

            // Step 3: If no new file was uploaded, update description only
            if (!hasNewFiles) {
                try (PreparedStatement updateDesc = conn.prepareStatement(
                        "UPDATE event_uploads SET description = ? WHERE event_id = ?")) {
                    updateDesc.setString(1, description);
                    updateDesc.setInt(2, eventId);
                    updateDesc.executeUpdate();
                }
            }

        } catch (SQLException e) {
            message = "Database error: " + e.getMessage();
            uploadSuccess = false;
        }

        if (uploadSuccess && message.isEmpty()) {
            message = "Event files and description updated successfully.";
        } else if (!message.isEmpty()) {
            message = "Update completed with issues:<br>" + message;
        }

        request.getSession().setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/teacher/eventDetails.jsp?eventId=" + eventId);
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    private boolean isAllowedFileType(String type) {
        return type != null && (type.equals("application/pdf") ||
                                type.equals("image/png") ||
                                type.equals("image/jpeg"));
    }
}
