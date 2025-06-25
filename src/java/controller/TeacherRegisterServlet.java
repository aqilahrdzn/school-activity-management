// src/main/java/controller/TeacherRegisterServlet.java
package controller;

import dao.TeacherDAO;
import model.Teacher;
import util.EmailUtil; // Import your EmailUtil class

import javax.servlet.*; 
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet; // This annotation requires Servlet API 3.0+

import java.time.Year;

@WebServlet("/TeacherRegisterServlet")
public class TeacherRegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contactNumber = request.getParameter("contact_number");
        String icNumber = request.getParameter("ic_number");
        String role = request.getParameter("role");
        String isGuruKelas = request.getParameter("is_guru_kelas");
        String kelas = request.getParameter("kelas");

        if (isGuruKelas == null) isGuruKelas = "No";
        if (kelas == null || kelas.trim().isEmpty()) kelas = null;

        // Auto set assigned year
        int currentYear = Year.now().getValue();

        // Create Teacher object
        Teacher teacher = new Teacher(name, email, password, contactNumber, icNumber, role);
        teacher.setIsGuruKelas(isGuruKelas);
        teacher.setKelas(kelas);
        teacher.setProfilePicture("default.png"); // Default profile picture
        teacher.setAssignedYear(currentYear);     // Set current year

        TeacherDAO teacherDao = new TeacherDAO();
        boolean isRegistered = teacherDao.insertTeacher(teacher);

        if (isRegistered) {
            EmailUtil.sendRegistrationEmail(email, name, password);
            response.sendRedirect("teacherList.jsp");
        } else {
            response.getWriter().write("Teacher registration failed. Please try again.");
        }
    }
}
