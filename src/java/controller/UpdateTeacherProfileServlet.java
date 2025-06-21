package controller;

import model.Teacher;
import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/UpdateTeacherProfileServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class UpdateTeacherProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Teacher teacher = (Teacher) session.getAttribute("teacher");

        if (teacher == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");

        Part filePart = request.getPart("profilePic");
        String fileName = null;
        String savedFileName = null;

        String uploadPath = getServletContext().getRealPath("/profile_pics");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null) {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_"); // Sanitize filename
            savedFileName = "teacher_" + teacher.getId() + "_" + System.currentTimeMillis() + "_" + fileName;

            // Delete old profile picture (if not default)
            String oldPic = teacher.getProfilePicture();
            if (oldPic != null && !oldPic.equals("default.jpg")) {
                File oldFile = new File(uploadPath + File.separator + oldPic);
                if (oldFile.exists()) oldFile.delete();
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                File file = new File(uploadPath + File.separator + savedFileName);
                Files.copy(fileContent, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                response.getWriter().println("File upload failed.");
                return;
            }
        }

        try (Connection conn = DBConfig.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (savedFileName != null) {
                sql = "UPDATE teachers SET name=?, email=?, profile_picture=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, savedFileName);
                stmt.setInt(4, teacher.getId());
            } else {
                sql = "UPDATE teachers SET name=?, email=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setInt(3, teacher.getId());
            }

            stmt.executeUpdate();
            teacher.setName(name);
            teacher.setEmail(email);
            if (savedFileName != null) teacher.setProfilePicture(savedFileName);
            session.setAttribute("teacher", teacher);
            response.sendRedirect("teacher/updateAccTc.jsp?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Update failed. Please try again.");
        }
    }
}
