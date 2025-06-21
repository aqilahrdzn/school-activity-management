// src/main/java/controller/TeacherRegisterServlet.java
package controller;

import dao.TeacherDAO;
import model.Teacher;
import util.EmailUtil; // Import your EmailUtil class

import javax.servlet.*; 
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet; // This annotation requires Servlet API 3.0+

@WebServlet("/TeacherRegisterServlet")
public class TeacherRegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); // This is the plain password from the form
        String contactNumber = request.getParameter("contact_number");
        String icNumber = request.getParameter("ic_number");
        String role = request.getParameter("role");
        String isGuruKelas = request.getParameter("is_guru_kelas");
        String kelas = request.getParameter("kelas");

        // Handle nulls if not selected
        if (isGuruKelas == null) {
            isGuruKelas = "No";
        }
        if (kelas == null) {
            kelas = "";
        }

        // Create a Teacher object
        Teacher teacher = new Teacher(name, email, password, contactNumber, icNumber, role);
        teacher.setIsGuruKelas(isGuruKelas);
        teacher.setKelas(kelas);

        TeacherDAO teacherDao = new TeacherDAO();
        boolean isRegistered = teacherDao.insertTeacher(teacher); // Assuming this saves the teacher and password

        if (isRegistered) {
            // Teacher successfully registered in the database.
            // Now, send the email with the password.
            EmailUtil.sendRegistrationEmail(email, name, password); // Send the plain password to the teacher

            // Redirect to a success page or login page
            response.sendRedirect("login.jsp");
        } else {
            // Registration failed
            response.getWriter().write("Teacher registration failed. Please try again.");
        }
    }
}