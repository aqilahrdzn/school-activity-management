package controller;

import dao.StudentDAO;
import model.Parent;
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
import java.util.List;
import model.Student;

@WebServlet("/UpdateParentProfileServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class UpdateParentProfileServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "profile_pics";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Parent parent = (Parent) session.getAttribute("parent");

        if (parent == null) {
            response.sendRedirect(request.getContextPath() + "login.jsp");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        boolean updatePassword = false;

        // Password Validation First
        if (!isEmpty(oldPassword) || !isEmpty(newPassword) || !isEmpty(confirmPassword)) {
            if (isEmpty(oldPassword) || isEmpty(newPassword) || isEmpty(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "parent/updateAccPr.jsp?error=All password fields are required.");
                return;
            }
            if (!oldPassword.equals(parent.getPassword())) {
                response.sendRedirect(request.getContextPath() + "parent/updateAccPr.jsp?error=Old password is incorrect.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "parent/updateAccPr.jsp?error=New password and confirm password do not match.");
                return;
            }
            updatePassword = true;
        }

        // Profile Picture
        Part filePart = request.getPart("profilePic");
        String fileName = null;
        String savedFileName = null;

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null) {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            savedFileName = "parent_" + parent.getId() + "_" + System.currentTimeMillis() + "_" + fileName;

            // Remove old profile pic
            String oldPic = parent.getProfilePicture();
            if (oldPic != null && !oldPic.equals("default.jpg")) {
                File oldFile = new File(uploadPath + File.separator + oldPic);
                if (oldFile.exists()) oldFile.delete();
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                File file = new File(uploadPath + File.separator + savedFileName);
                Files.copy(fileContent, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                response.getWriter().println("Failed to save the profile picture.");
                return;
            }
        }

        try (Connection conn = DBConfig.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE parent SET name=?, email=?");
            if (savedFileName != null) sql.append(", profile_picture=?");
            if (updatePassword) sql.append(", password=?");
            sql.append(" WHERE id=?");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int idx = 1;
            stmt.setString(idx++, name);
            stmt.setString(idx++, email);
            if (savedFileName != null) stmt.setString(idx++, savedFileName);
            if (updatePassword) stmt.setString(idx++, newPassword);
            stmt.setInt(idx, parent.getId());

            stmt.executeUpdate();
            stmt.close();

            // Update session
            parent.setName(name);
            parent.setEmail(email);
            if (savedFileName != null) parent.setProfilePicture(savedFileName);
            if (updatePassword) parent.setPassword(newPassword);
            session.setAttribute("parent", parent);

            response.sendRedirect("parent/updateAccPr.jsp?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("parent/updateAccPr.jsp?error=Update failed.");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
