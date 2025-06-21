/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author Lenovo
 */
import dao.ReminderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Reminder;

@WebServlet("/updateReminderServlet")
public class ReminderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String activityName = request.getParameter("activityName");
        String activityDate = request.getParameter("activityDate");
        String reminderDetails = request.getParameter("reminderDetails");

        Reminder reminder = new Reminder(activityName, activityDate, reminderDetails);

        // Save reminder to database
        ReminderDAO dao = new ReminderDAO();
        boolean success = dao.saveReminder(reminder);

        if (success) {
            response.sendRedirect("success.jsp");
        } else {
            response.sendRedirect("error.jsp");
        }
    }
}

