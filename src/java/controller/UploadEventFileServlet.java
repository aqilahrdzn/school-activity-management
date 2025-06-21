package controller;

import java.io.*;
import java.sql.*;
import java.util.Collection; // Import Collection
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import util.DBConfig;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,    // 2MB
    maxFileSize = 1024 * 1024 * 10,         // 10MB (Per file)
    maxRequestSize = 1024 * 1024 * 50       // 50MB (Total request size)
)
@WebServlet("/UploadEventFileServlet")
public class UploadEventFileServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Upload path
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        String eventIdParam = request.getParameter("eventId");
        int eventId = (eventIdParam != null && !eventIdParam.isEmpty()) ? Integer.parseInt(eventIdParam) : 0;
        String description = request.getParameter("description");

        String message = "";
        boolean uploadSuccess = true;

        // Get all parts for the file input name "eventFile"
        Collection<Part> fileParts = request.getParts(); // Get all parts first

        try (Connection conn = DBConfig.getConnection()) {
            // SQL for inserting new file uploads
            String sql = "INSERT INTO event_uploads (event_id, file_name, file_path, file_type, description) VALUES (?, ?, ?, ?, ?)";
            // Use batch updates for efficiency if many files are expected
            // conn.setAutoCommit(false); // Optional: for transaction management

            for (Part part : fileParts) {
                // Check if this part is an actual file upload from our specific input name
                if (part.getName().equals("eventFile") && part.getSize() > 0) {
                    String fileName = extractFileName(part);
                    String fileType = part.getContentType();

                    if (!isAllowedFileType(fileType)) {
                        message += "Skipped '" + fileName + "': Only PDF, PNG, JPG, JPEG files are allowed.<br>";
                        uploadSuccess = false;
                        continue; // Skip this file, proceed with others
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
                        message += "Failed to save '" + fileName + "' to disk: " + e.getMessage() + "<br>";
                        uploadSuccess = false;
                        continue; // Skip this file, proceed with others
                    }

                    // Save to database
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, eventId);
                        stmt.setString(2, fileName);
                        stmt.setString(3, UPLOAD_DIR + "/" + fileName); // Save relative path
                        stmt.setString(4, fileType);
                        stmt.setString(5, description); // Save description for each file (or adjust if description is per-event)
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        message += "Failed to save '" + fileName + "' to database: " + e.getMessage() + "<br>";
                        uploadSuccess = false;
                        // conn.rollback(); // Optional: rollback transaction on error
                        continue; // Skip this file, proceed with others
                    }
                }
            }
            // conn.commit(); // Optional: for transaction management
        } catch (SQLException e) {
            message = "Database connection error: " + e.getMessage();
            uploadSuccess = false;
        }

        if (uploadSuccess && message.isEmpty()) {
            message = "All selected files uploaded and saved successfully.";
        } else if (uploadSuccess && !message.isEmpty()) {
            message = "Some files were processed, but with warnings:<br>" + message;
        } else {
            if (message.isEmpty()) {
                message = "No files were uploaded or processed.";
            } else {
                message = "Errors occurred during file upload:<br>" + message;
            }
        }

        request.getSession().setAttribute("message", message); // Use session to carry message across redirect
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