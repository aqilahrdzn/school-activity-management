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

        // Get form inputs
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        boolean updatePassword = false;

        // ✅ STEP 1: Check password early
        if (!isEmpty(oldPassword) || !isEmpty(newPassword) || !isEmpty(confirmPassword)) {
            if (isEmpty(oldPassword) || isEmpty(newPassword) || isEmpty(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "teacher/updateAccTc.jsp?error=All password fields are required.");
                return;
            }

            if (!oldPassword.equals(teacher.getPassword())) {
                response.sendRedirect(request.getContextPath() + "teacher/updateAccTc.jsp?error=Old password is incorrect.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "teacher/updateAccTc.jsp?error=New password and confirmation do not match.");
                return;
            }

            updatePassword = true;
        }

        // ✅ STEP 2: Handle profile picture (only proceed if password is OK)
        Part filePart = request.getPart("profilePic");
        String fileName = null;
        String savedFileName = null;

        String uploadPath = getServletContext().getRealPath("/profile_pics");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null) {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            savedFileName = "teacher_" + teacher.getId() + "_" + System.currentTimeMillis() + "_" + fileName;

            // Delete old picture if needed
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
            StringBuilder sql = new StringBuilder("UPDATE teachers SET email=?, contact_number=?");
            if (savedFileName != null) sql.append(", profile_picture=?");
            if (updatePassword) sql.append(", password=?");
            sql.append(" WHERE id=?");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int index = 1;
            stmt.setString(index++, email);
            stmt.setString(index++, phone);
            if (savedFileName != null) stmt.setString(index++, savedFileName);
            if (updatePassword) stmt.setString(index++, newPassword);
            stmt.setInt(index, teacher.getId());

            stmt.executeUpdate();

            // ✅ Update session teacher
            teacher.setEmail(email);
            teacher.setContactNumber(phone);
            if (savedFileName != null) teacher.setProfilePicture(savedFileName);
            if (updatePassword) teacher.setPassword(newPassword);
            session.setAttribute("teacher", teacher);

            response.sendRedirect(request.getContextPath() + "teacher/updateAccTc.jsp?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "teacher/updateAccTc.jsp?error=Update failed. Please try again.");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
