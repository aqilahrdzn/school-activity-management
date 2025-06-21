/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


import util.DBConfig;

/**
 *
 * @author Lenovo
 */
@WebServlet("/SubmitApprovalServlet")
public class SubmitApprovalServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int parentId = Integer.parseInt(request.getParameter("parent_id"));
        int eventId = Integer.parseInt(request.getParameter("event_id"));
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");

        try (Connection con = DBConfig.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO parent_approval (parent_id, event_id, status, reason, approved_at) VALUES (?, ?, ?, ?, NOW())"
            );
            ps.setInt(1, parentId);
            ps.setInt(2, eventId);
            ps.setString(3, status);
            ps.setString(4, reason != null ? reason : ""); // Elakkan null

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("DB Error: " + e.getMessage());
            return;
        }

        response.sendRedirect("parent/studentEvent.jsp?success=true");

    }
}
