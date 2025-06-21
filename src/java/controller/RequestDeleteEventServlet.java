/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/RequestDeleteEventServlet")
public class RequestDeleteEventServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RequestDeleteEventServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventId = request.getParameter("eventId");

        if (eventId == null || eventId.isEmpty()) {
            response.sendRedirect("viewEvent.jsp?error=Invalid event ID");
            return;
        }

        String updateStatusQuery = "UPDATE events SET status = 'cancellation requested' WHERE id = ?";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateStatusQuery)) {

            stmt.setString(1, eventId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                response.sendRedirect("viewEvent.jsp?message=Cancellation request submitted successfully");
            } else {
                response.sendRedirect("viewEvent.jsp?error=Failed to submit cancellation request");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error processing cancellation request for event ID: " + eventId, e);
            response.sendRedirect("viewEvent.jsp?error=An error occurred while processing your request");
        }
    }
}
