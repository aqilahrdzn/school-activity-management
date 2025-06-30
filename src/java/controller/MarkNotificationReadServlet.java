/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.NotificationDAO;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Notification;

/**
 *
 * @author Lenovo
 */
@WebServlet("/MarkNotificationRead")
public class MarkNotificationReadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        int eventId = -1; // default invalid

        if (idStr != null) {
            try {
                int notificationId = Integer.parseInt(idStr);
                NotificationDAO dao = new NotificationDAO();

                // Mark as read
                dao.markAsRead(notificationId);

                // Fetch event ID from notification
                Notification note = dao.getNotificationById(notificationId);
                if (note != null) {
                    eventId = note.getEventId();
                }

            } catch (NumberFormatException e) {
                e.printStackTrace(); // log error
            }
        }


        // ✅ Get role from session to determine redirect path
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if ("teacher".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/teacher/parentApprovals.jsp?eventId=" + eventId);
        } else if ("parent".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/parent/studentEvent.jsp");
        } else {
            // Default redirect if no role found
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}

