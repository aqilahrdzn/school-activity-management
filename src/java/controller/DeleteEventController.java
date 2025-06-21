package controller;

import dao.EventDAO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/DeleteEventController")
public class DeleteEventController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventId = request.getParameter("eventId");

        if (eventId == null || eventId.isEmpty()) {
            // If eventId is not provided, redirect to error page
            response.sendRedirect("errorPage.jsp?error=Event ID is required");
            return;
        }

        try {
            // Delete event from the database
            EventDAO eventDAO = new EventDAO();
            boolean isDeletedFromDB = eventDAO.deleteEventById(eventId);

            if (!isDeletedFromDB) {
                // If deletion fails, redirect to error page
                response.sendRedirect("errorPage.jsp?error=Failed to delete the event from the database.");
                return;
            }

            // Proceed to delete event from Google Calendar using NoCodeAPI
            String apiUrl = "https://v1.nocodeapi.com/aqilah/calendar/mtudlycLrYWeEomM/event?eventId=" + eventId;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                // Success: redirect to success page
                response.sendRedirect("successPage.jsp?message=Event deleted successfully from both the database and Google Calendar.");
            } else {
                // Read error response if deletion from Google Calendar fails
                InputStream errorStream = conn.getErrorStream();
                String errorMessage = "";
                if (errorStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        errorMessage = sb.toString();
                    }
                }
                // Redirect to error page if Google Calendar deletion fails
                response.sendRedirect("errorPage.jsp?error=Event deleted from the database, but failed to delete from Google Calendar. HTTP error code: " 
                                      + responseCode + " Error: " + errorMessage);
            }
        } catch (Exception e) {
            // Handle exceptions and redirect to error page
            response.sendRedirect("errorPage.jsp?error=An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
