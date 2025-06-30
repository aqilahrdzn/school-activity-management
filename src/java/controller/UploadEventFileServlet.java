package controller;

import java.io.*;
import java.sql.*;
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
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        int eventId = Integer.parseInt(request.getParameter("eventId"));
        String description = request.getParameter("description");

        Part part1 = request.getPart("eventFile1");
        Part part2 = request.getPart("eventFile2");
        Part part3 = request.getPart("eventFile3");

        String message = "";
        boolean uploadSuccess = true;

        try (Connection conn = DBConfig.getConnection()) {

            // Delete old entry (to support "edit")
            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM event_uploads WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // Save files to disk
            String fileName1 = saveFile(part1, uploadPath);
            String fileName2 = saveFile(part2, uploadPath);
            String fileName3 = saveFile(part3, uploadPath);

            String filePath1 = UPLOAD_DIR + "/" + fileName1;
            String filePath2 = UPLOAD_DIR + "/" + fileName2;
            String filePath3 = UPLOAD_DIR + "/" + fileName3;

            String fileType1 = part1.getContentType();
            String fileType2 = part2.getContentType();
            String fileType3 = part3.getContentType();

            // Insert metadata only (no blobs)
            String insertSQL = "INSERT INTO event_uploads (event_id, description, "
                    + "file_name1, file_path1, file_type1, "
                    + "file_name2, file_path2, file_type2, "
                    + "file_name3, file_path3, file_type3) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setInt(1, eventId);
                insertStmt.setString(2, description);

                insertStmt.setString(3, fileName1);
                insertStmt.setString(4, filePath1);
                insertStmt.setString(5, fileType1);

                insertStmt.setString(6, fileName2);
                insertStmt.setString(7, filePath2);
                insertStmt.setString(8, fileType2);

                insertStmt.setString(9, fileName3);
                insertStmt.setString(10, filePath3);
                insertStmt.setString(11, fileType3);

                // ✅ This executes the SQL
                insertStmt.executeUpdate();
            }

        } catch (SQLException | IOException e) {
            uploadSuccess = false;
            message = "Error: " + e.getMessage();
            e.printStackTrace();
        }

        if (uploadSuccess) {
            message = "Files and description uploaded successfully.";
        }

        request.getSession().setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/teacher/eventDetails.jsp?eventId=" + eventId);
    }

    private String saveFile(Part part, String uploadPath) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        String fileName = extractFileName(part);
        if (!isAllowedFileType(part.getContentType())) {
            return null;
        }

        String fullPath = uploadPath + File.separator + fileName;
        try (InputStream input = part.getInputStream(); FileOutputStream output = new FileOutputStream(fullPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return fileName;
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return null;
    }

    private boolean isAllowedFileType(String type) {
        return type != null && (type.equals("image/jpeg") || type.equals("image/png"));
    }
}
