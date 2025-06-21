// src/main/java/controller/StudentEditController.java
package controller;

import dao.StudentDAO;
import model.Student;
import model.Teacher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Make sure this import is there
import java.io.IOException;
import java.net.URLEncoder; // For encoding messages in URL parameters
import java.nio.charset.StandardCharsets; // For URL encoding charset

@WebServlet("/StudentEditController")
public class StudentEditController extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        // Initialize your DAO when the servlet starts
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Use request.getSession(false) to retrieve an existing session
        HttpSession session = request.getSession(false);

        // 1. Session and Teacher Login Check
        // CRITICAL FIX: Use "teacher" as the attribute key, and getSession(false)
        Teacher loggedInTeacher = (Teacher) session.getAttribute("teacher"); // FIX: Changed "loggedInTeacher" to "teacher"

        if (session == null || loggedInTeacher == null) { // loggedInTeacher will be null if session is null or attribute is missing
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return; // Stop further processing
        }

        boolean isCurrentUserGuruKelas = "Yes".equals(loggedInTeacher.getIsGuruKelas());
        String currentUserKelas = loggedInTeacher.getKelas();

        String action = request.getParameter("action");
        String studentIdParam = request.getParameter("studentId"); // For GET requests, student ID comes from URL

        if ("edit".equals(action) && studentIdParam != null && !studentIdParam.isEmpty()) {
            try {
                int studentId = Integer.parseInt(studentIdParam);
                Student student = studentDAO.getStudentById(studentId); // Get the student from DB

                if (student != null) {
                    // 2. Authorization Check (Is this Guru Kelas allowed to edit THIS student?)
                    if (isCurrentUserGuruKelas && student.getStudentClass().equals(currentUserKelas)) {
                        request.setAttribute("student", student);
                        request.getRequestDispatcher("teacher/editStudent.jsp").forward(request, response);
         
                    } else {
                        // Not authorized to edit this specific student
                        String errorMessage = "You are not authorized to edit students from class " + student.getStudentClass() + ".";
                        // Redirect back to student list, passing error message
                        response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                    }
                } else {
                    // Student not found
                    String errorMessage = "Student with ID " + studentIdParam + " not found.";
                    response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                }
            } catch (NumberFormatException e) {
                // Invalid studentId format
                String errorMessage = "Invalid student ID format provided.";
                response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
            }
        } else {
            // No 'edit' action or missing studentId parameter
            String errorMessage = "Invalid request to edit student.";
            response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
        }
    }

     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Teacher loggedInTeacher = (Teacher) session.getAttribute("teacher");

        if (session == null || loggedInTeacher == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        boolean isCurrentUserGuruKelas = "Yes".equals(loggedInTeacher.getIsGuruKelas());
        String currentUserKelas = loggedInTeacher.getKelas();

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            try {
                int studentId = Integer.parseInt(request.getParameter("id"));
                String studentName = request.getParameter("studentName");
                String icNumber = request.getParameter("icNumber");
                String sportTeam = request.getParameter("sportTeam");
                String uniformUnit = request.getParameter("uniformUnit");
                String studentClass = request.getParameter("studentClass"); // Hidden field from form

                // Get the student's current details from DB to re-verify class for authorization
                Student originalStudent = studentDAO.getStudentById(studentId);

                if (originalStudent != null) {
                    // 2. Authorization Check for Update (CRUCIAL)
                    if (isCurrentUserGuruKelas && originalStudent.getStudentClass().equals(currentUserKelas)) {
                        // If the user is authorized for this student, populate the updated data
                        Student updatedStudent = new Student(); // This is the correct object name
                        updatedStudent.setId(studentId);
                        updatedStudent.setStudentName(studentName);
                        updatedStudent.setIcNumber(icNumber);
                        updatedStudent.setSportTeam(sportTeam);
                        updatedStudent.setUniformUnit(uniformUnit);
                        // Ensure class property is from original student or validated (not from request directly)
                        updatedStudent.setStudentClass(originalStudent.getStudentClass());

                        boolean success = studentDAO.updateStudent(updatedStudent);

                        if (success) {
                            String successMessage = "Student details updated successfully!";
                            // Redirect back to the student list for the updated class
                            response.sendRedirect(request.getContextPath() + "/StudentListController?studentClass=" + originalStudent.getStudentClass() + "&successMessage=" + URLEncoder.encode(successMessage, StandardCharsets.UTF_8.toString()));
                        } else {
                            String errorMessage = "Failed to update student details in the database.";
                            request.setAttribute("errorMessage", errorMessage);
                            // On failure, re-display the edit form with the error and the data they tried to submit
                            request.setAttribute("student", updatedStudent); // FIX: Changed to 'updatedStudent'
                            request.getRequestDispatcher("teacher/editStudent.jsp").forward(request, response);
     
                        }
                    } else {
                        // Not authorized to update this specific student
                        String errorMessage = "You are not authorized to update students from this class.";
                        response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                    }
                } else {
                    String errorMessage = "Original student record not found for update (ID: " + studentId + ").";
                    response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                }
            } catch (NumberFormatException e) {
                String errorMessage = "Invalid student ID format in update request.";
                response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
            }
        } else {
            // Invalid action for POST
            String errorMessage = "Invalid action for student update.";
            response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
        }
    }

}