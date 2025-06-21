package controller;

import dao.StudentDAO;
import model.Student;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/StudentRegistrationServlet")
public class StudentRegistrationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentClass = request.getParameter("class");
        String studentName = request.getParameter("studentname");
        String icNumber = request.getParameter("ic");
        String sportTeam = request.getParameter("sport_team");
        String uniformUnit = request.getParameter("uniform_unit");

        // Get the logged-in teacher's class from session
        String teacherAssignedClass = (String) request.getSession().getAttribute("teacherClass");

        // Validation: Only allow registration for teacher's own class
        if (!studentClass.equals(teacherAssignedClass)) {
            request.getSession().setAttribute("errorMessage", "You are not authorized to register students for this class!");
            response.sendRedirect(request.getContextPath() + "/teacher/studentRegistration.jsp");
            return;
        }

        // Validate form input
        if (studentClass == null || studentClass.isEmpty()
                || studentName == null || studentName.isEmpty()
                || icNumber == null || icNumber.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required!");
            request.getRequestDispatcher("teacher/studentRegistration.jsp").forward(request, response);
            return;
        }

        // Save student
        Student student = new Student(studentClass, studentName, icNumber, sportTeam, uniformUnit);
        StudentDAO studentDAO = new StudentDAO();
        boolean isRegistered = studentDAO.registerStudent(student);

        if (isRegistered) {
            request.getSession().setAttribute("successMessage", "Student registered successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Registration failed! Please try again.");
        }
        response.sendRedirect(request.getContextPath() + "/teacher/studentRegistration.jsp");
    }

}
