/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ClassroomDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lenovo
 */
@WebServlet("/staff-cancel")
public class StaffCancelController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            ClassroomDAO dao = new ClassroomDAO();
            dao.approveCancellation(bookingId);
            response.sendRedirect("staff_view.jsp");
        } catch (Exception e) {
            throw new ServletException("Failed to approve cancellation", e);
        }
    }
}
