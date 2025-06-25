/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.StudentDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to handle student archiving.
 */
@WebServlet(name = "ArchiveStudentServlet", urlPatterns = {"/teacher/archiveStudent"})
public class ArchiveStudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String icNumber = request.getParameter("ic");
        String redirectURL = request.getContextPath() + "/teacher/studentList.jsp";
        
        if (icNumber == null || icNumber.trim().isEmpty()) {
            response.sendRedirect(redirectURL + "?errorMessage=Student IC number is required for archiving.");
            return;
        }

        try {
            StudentDAO studentDAO = new StudentDAO();
            boolean archived = studentDAO.archiveStudent(icNumber);

            if (archived) {
                response.sendRedirect(redirectURL + "?successMessage=Student has been successfully archived.");
            } else {
                response.sendRedirect(redirectURL + "?errorMessage=Failed to archive student.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectURL + "?errorMessage=An error occurred while archiving: " + e.getMessage());
        }
    }
}