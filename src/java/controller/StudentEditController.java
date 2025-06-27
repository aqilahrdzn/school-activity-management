package controller;

import dao.StudentDAO;
import model.Student;
import model.Teacher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/StudentEditController")
public class StudentEditController extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Teacher loggedInTeacher = (session != null) ? (Teacher) session.getAttribute("teacher") : null;

        if (session == null || loggedInTeacher == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        boolean isCurrentUserGuruKelas = "Yes".equals(loggedInTeacher.getIsGuruKelas());
        String currentUserKelas = loggedInTeacher.getKelas();

        String action = request.getParameter("action");
        String studentIdParam = request.getParameter("studentId");

        if ("edit".equals(action) && studentIdParam != null && !studentIdParam.isEmpty()) {
            try {
                int studentId = Integer.parseInt(studentIdParam);
                Student student = studentDAO.getStudentById(studentId);

                if (student != null) {
                    if (isCurrentUserGuruKelas && student.getStudentClass().equals(currentUserKelas)) {
                        request.setAttribute("student", student);
                        request.getRequestDispatcher("/teacher/editStudent.jsp").forward(request, response);
                    } else {
                        String errorMessage = "You are not authorized to edit students from class " + student.getStudentClass() + ".";
                        response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                    }
                } else {
                    String errorMessage = "Student with ID " + studentIdParam + " not found.";
                    response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
                }
            } catch (NumberFormatException e) {
                String errorMessage = "Invalid student ID format provided.";
                response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
            }
        } else {
            String errorMessage = "Invalid request to edit student.";
            response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Teacher loggedInTeacher = (session != null) ? (Teacher) session.getAttribute("teacher") : null;

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

                Student originalStudent = studentDAO.getStudentById(studentId);

                if (originalStudent != null) {
                    if (isCurrentUserGuruKelas && originalStudent.getStudentClass().equals(currentUserKelas)) {
                        Student updatedStudent = new Student();
                        updatedStudent.setId(studentId);
                        updatedStudent.setStudentName(studentName);
                        updatedStudent.setIcNumber(icNumber);
                        updatedStudent.setSportTeam(sportTeam);
                        updatedStudent.setUniformUnit(uniformUnit);
                        updatedStudent.setStudentClass(originalStudent.getStudentClass());

                        boolean success = studentDAO.updateStudent(updatedStudent);

                        if (success) {
                            String successMessage = "Student details updated successfully!";
                            response.sendRedirect(request.getContextPath() + "/StudentListController?studentClass=" + originalStudent.getStudentClass() + "&successMessage=" + URLEncoder.encode(successMessage, StandardCharsets.UTF_8.toString()));
                        } else {
                            String errorMessage = "Failed to update student details in the database.";
                            request.setAttribute("errorMessage", errorMessage);
                            request.setAttribute("student", updatedStudent);
                            request.getRequestDispatcher("/teacher/editStudent.jsp").forward(request, response);
                        }
                    } else {
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
            String errorMessage = "Invalid action for student update.";
            response.sendRedirect(request.getContextPath() + "/StudentListController?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
        }
    }
}
