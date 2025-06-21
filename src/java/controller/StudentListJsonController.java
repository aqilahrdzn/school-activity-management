// src/main/java/controller/StudentListJsonController.java
package controller;

import dao.StudentDAO;
import model.Student;
import model.Teacher; // Assuming Teacher model
import com.google.gson.Gson; // You'll need Gson library in your classpath (e.g., Maven dependency)

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList; // For empty list

@WebServlet("/StudentListJsonController") // A new servlet for JSON data
public class StudentListJsonController extends HttpServlet {

    private StudentDAO studentDAO;
    private Gson gson; // For converting Java objects to JSON

    @Override
    public void init() throws ServletException {
        super.init();
        studentDAO = new StudentDAO();
        gson = new Gson(); // Initialize Gson
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Basic authentication check
        if (session == null || session.getAttribute("teacher") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Unauthorized access.");
            return;
        }

        Teacher loggedInTeacher = (Teacher) session.getAttribute("teacher");
        boolean isGuruKelas = "Yes".equals(loggedInTeacher.getIsGuruKelas());
        String currentUserKelas = loggedInTeacher.getKelas();

        String selectedClass = request.getParameter("studentClass");
        List<Student> students = new ArrayList<>(); // Initialize as empty list

        if (selectedClass != null && !selectedClass.isEmpty()) {
            students = studentDAO.getStudentsByClass(selectedClass);

            // Important: Apply server-side filtering for Guru Kelas if needed
            // If a Guru Kelas tries to view students of OTHER classes,
            // you might want to return an empty list or only allow viewing their own.
            // For now, let's allow viewing all but control editing on client side.
            // Server-side editing control is still in StudentEditController.
        }

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(students)); // Convert list of students to JSON and send
        out.flush();
    }
}