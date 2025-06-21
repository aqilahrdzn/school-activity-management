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
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Parent parent = (Parent) session.getAttribute("parent");

        if (parent == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        Part filePart = request.getPart("profilePic");

        String fileName = null;
        String savedFileName = null;

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            savedFileName = "parent_" + parent.getId() + "_" + System.currentTimeMillis() + "_" + fileName;

            // Delete old profile picture if it's not default
            String oldPic = parent.getProfilePicture();
            if (oldPic != null && !oldPic.equals("default.jpg")) {
                File oldFile = new File(uploadPath + File.separator + oldPic);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                File newFile = new File(uploadPath + File.separator + savedFileName);
                Files.copy(fileContent, newFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                response.getWriter().println("Failed to save the profile picture.");
                return;
            }
        }

        try (Connection conn = DBConfig.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (savedFileName != null) {
                sql = "UPDATE parent SET name=?, email=?, profile_picture=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, savedFileName);
                stmt.setInt(4, parent.getId());
            } else {
                sql = "UPDATE parent SET name=?, email=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setInt(3, parent.getId());
            }

            stmt.executeUpdate();
            stmt.close();

            // Update session object
            parent.setName(name);
            parent.setEmail(email);
            if (savedFileName != null) {
                parent.setProfilePicture(savedFileName);
            }
            session.setAttribute("parent", parent);

            StudentDAO studentDAO = new StudentDAO();
            List<Student> children = studentDAO.getStudentsByParentId(parent.getId());
            request.setAttribute("children", children);

// Forward to JSP to show updated info and children list
            request.setAttribute("success", true); // pass success flag as request attribute
            response.sendRedirect(request.getContextPath() + "/parent/updateAccPr.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Update failed. Please try again.");
        }
    }
}
