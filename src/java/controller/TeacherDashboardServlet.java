/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.TeacherDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Teacher;

/**
 *
 * @author Lenovo
 */
@WebServlet(name = "TeacherDashboardServlet", urlPatterns = {"/TeacherDashboardServlet"})
public class TeacherDashboardServlet extends HttpServlet {

  
     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email != null) {
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.getTeacherDetails(email);

            if (teacher != null) {
                request.setAttribute("teacher", teacher); // Pass teacher details to JSP
                RequestDispatcher dispatcher = request.getRequestDispatcher("teacherDashboard.jsp");
                dispatcher.forward(request, response);
            } else {
                response.sendRedirect("error.jsp");
            }
        } else {
            response.sendRedirect("login.jsp");
        }
    }

   

}
