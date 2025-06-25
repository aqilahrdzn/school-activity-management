// src/main/java/controller/StudentListJsonController.java
package controller;

import dao.StudentDAO;
import model.Student;
import model.Teacher;
import com.google.gson.Gson; // Ensure Gson library is in your classpath

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * This servlet handles AJAX requests from studentList.jsp.
 * It fetches a list of students for a given class from the DAO
 * and returns them as a JSON array.
 */
@WebServlet("/StudentListJsonController")
public class StudentListJsonController extends HttpServlet {

    private StudentDAO studentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        // Initialize DAO and Gson instances once when the servlet is created.
        super.init();
        studentDAO = new StudentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Security check: Ensure a teacher is logged in.
        if (session == null || session.getAttribute("teacher") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write(gson.toJson("Error: Unauthorized access. Please log in."));
            return;
        }

        // Get the class parameter from the AJAX request
        String selectedClass = request.getParameter("studentClass");
        List<Student> students = new ArrayList<>();

        if (selectedClass != null && !selectedClass.trim().isEmpty()) {
            try {
                // --- THIS IS THE MOST IMPORTANT PART ---
                // This line calls your StudentDAO. The logic to fetch ONLY students
                // with `status = 'active'` MUST be inside the SQL query of the
                // `getStudentsByClass` method in your StudentDAO.java file.
                //
                // This controller correctly calls the method; the DAO method itself
                // must perform the correct filtering.
                students = studentDAO.getStudentsByClass(selectedClass);

            } catch (Exception e) {
                // Handle potential SQL errors from the DAO
                e.printStackTrace(); // Log the full error to the server console
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson("Error: Could not retrieve student data."));
                return;
            }
        }

        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert the list of students (which may be empty) to a JSON string and send it
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(students));
        out.flush();
    }
}
