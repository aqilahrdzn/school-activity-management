/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/PromoteStudentsServlet")
public class PromoteStudentsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StudentDAO dao = new StudentDAO();
        boolean success = dao.promoteStudents();

        if (success) {
            request.getSession().setAttribute("successMessage", "Students promoted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Promotion failed!");
        }

        response.sendRedirect(request.getContextPath() + "/clerk/studentList.jsp"); // adjust this path if needed
    }
}
