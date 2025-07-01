/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ClassroomDAO;
import model.Classroom;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/AddClassroomController")
public class AddClassroomController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        int capacity = Integer.parseInt(request.getParameter("capacity"));

        Classroom newClassroom = new Classroom(name, capacity);

        try {
            ClassroomDAO dao = new ClassroomDAO();
            dao.addClassroom(newClassroom);
            response.sendRedirect(request.getContextPath() + "/clerk/addVenue.jsp?success=1");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/clerk/addVenue.jsp?status=error");
        }
    }
}
