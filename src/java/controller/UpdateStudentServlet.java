/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.StudentDAO;
import model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UpdateStudentServlet")
public class UpdateStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        try {
            // Get form parameters
            int id = Integer.parseInt(request.getParameter("id"));
            String studentName = request.getParameter("studentName");
            String icNumber = request.getParameter("icNumber");
            String studentClass = request.getParameter("studentClass");
            String sportTeam = request.getParameter("sportTeam");
            String uniformUnit = request.getParameter("uniformUnit");

            // Create updated student object
            Student student = new Student();
            student.setId(id);
            student.setStudentName(studentName);
            student.setIcNumber(icNumber);
            student.setStudentClass(studentClass);
            student.setSportTeam(sportTeam);
            student.setUniformUnit(uniformUnit);

            // Call DAO to update student
            StudentDAO studentDAO = new StudentDAO();
            studentDAO.updateStudent(student);

            // Redirect or forward to confirmation or list page
            response.sendRedirect("/teacher/studentList.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to update student: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
