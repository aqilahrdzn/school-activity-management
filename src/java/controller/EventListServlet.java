/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.EventDAO;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import model.Event;


@WebServlet("/event-details")
public class EventListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    EventDAO dao = new EventDAO();
    List<Event> events = dao.getAllEvents();

    // DEBUG PRINT
    System.out.println("DEBUG: Retrieved " + events.size() + " events.");
    for (Event e : events) {
        System.out.println("Event title: " + e.getTitle());
    }

    request.setAttribute("events", events);
    RequestDispatcher dispatcher = request.getRequestDispatcher("event-details.jsp");
    dispatcher.forward(request, response);
}

}

