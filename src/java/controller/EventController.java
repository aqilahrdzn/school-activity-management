// Updated EventController.java (Surat Pengesahan and Email sending removed)
package controller;

import dao.EventDAO;
import dao.StudentDAO;
import dao.EventParticipantDAO;
import model.Event;
import model.Student;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@WebServlet("/EventController")
public class EventController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("getStudentByIC".equals(action)) {
            String ic = request.getParameter("ic");
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.getStudentByIC(ic);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject jsonResponse = new JSONObject();

            if (student != null) {
                jsonResponse.put("success", true);
                jsonResponse.put("name", student.getStudentName());
                jsonResponse.put("ic", student.getIcNumber());
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Student with IC " + ic + " not found.");
            }
            out.print(jsonResponse.toString());
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        EventDAO eventDAO = new EventDAO();

        if ("delete".equals(action)) {
            int eventId = Integer.parseInt(request.getParameter("eventId"));
            boolean deleted = eventDAO.deleteEventById(eventId);
            response.sendRedirect("teacher/eventList.jsp?status=" + (deleted ? "deleted" : "deletefail"));
            return;
        }

        if ("update".equals(action)) {
            String eventIdStr = request.getParameter("eventId");
            if (eventIdStr != null && !eventIdStr.isEmpty()) {
                int eventId = Integer.parseInt(eventIdStr);

                String category = request.getParameter("event-category");
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String startTimeStr = request.getParameter("startTime");
                String endTimeStr = request.getParameter("endTime");
                String timeZone = request.getParameter("timeZone");
                double paymentAmount = 0.0;

                if (startTimeStr.length() == 16) {
                    startTimeStr += ":00";
                }
                if (endTimeStr.length() == 16) {
                    endTimeStr += ":00";
                }

                try {
                    LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
                    if (startTime.isBefore(LocalDateTime.now())) {
                        response.getWriter().println("Error: Start time cannot be in the past.");
                        return;
                    }
                } catch (DateTimeParseException e) {
                    response.getWriter().println("Error: Invalid date/time format.");
                    return;
                }

                if ("payment".equals(category)) {
                    String amountStr = request.getParameter("paymentAmount");
                    if (amountStr != null && !amountStr.trim().isEmpty()) {
                        try {
                            paymentAmount = Double.parseDouble(amountStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid payment amount: " + amountStr);
                        }
                    }
                }

                Event event = new Event();
                event.setId(eventIdStr);
                event.setCategory(category);
                event.setTitle(title);
                event.setDescription(description);
                event.setStartTime(startTimeStr);
                event.setEndTime(endTimeStr);
                event.setTimeZone(timeZone);
                event.setPaymentAmount(paymentAmount);

                boolean updated = eventDAO.updateEvent(event);
                response.sendRedirect("teacher/eventList.jsp?status=" + (updated ? "updated" : "updatefail"));
                return;
            } else {
                response.getWriter().println("Missing event ID for update.");
                return;
            }
        }

        // Proceed with insert logic
        String category = request.getParameter("event-category");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String timeZone = request.getParameter("timeZone");

        System.out.println("DEBUG - Received category: " + category);
        System.out.println("DEBUG - Title: " + title + ", StartTime: " + startTimeStr + ", EndTime: " + endTimeStr);

        if (startTimeStr.length() == 16) {
            startTimeStr += ":00";
        }
        if (endTimeStr.length() == 16) {
            endTimeStr += ":00";
        }

        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            if (startTime.isBefore(LocalDateTime.now())) {
                System.out.println("DEBUG - Start time is in the past.");
                response.getWriter().println("Error: Start time cannot be in the past.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("DEBUG - DateTimeParseException: " + e.getMessage());
            response.getWriter().println("Error: Invalid date/time format.");
            return;
        }

        String createdBy = (String) request.getSession().getAttribute("email");
        String selectionType = request.getParameter("selectType");
        System.out.println("DEBUG - Selection Type: " + selectionType);

        EventParticipantDAO participantDAO = new EventParticipantDAO();

        double paymentAmount = 0.0;
        if ("payment".equals(category)) {
            String amountStr = request.getParameter("paymentAmount");
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    paymentAmount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    System.out.println("DEBUG - Invalid payment amount: " + amountStr);
                }
            }
        }

        Event event = new Event();
        event.setCategory(category);
        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTimeStr);
        event.setEndTime(endTimeStr);
        event.setTimeZone(timeZone);
        event.setCreatedBy(createdBy);
        event.setPaymentAmount(paymentAmount);

        String[] selectedClasses = request.getParameterValues("classDropdown");
        String[] selectedICsFromIndividual = request.getParameterValues("selectedICs");
        String sportTeam = request.getParameter("sportDropdown");
        String uniformUnit = request.getParameter("uniformDropdown");

        event.setTargetClass(String.join(", ", selectedClasses != null ? selectedClasses : new String[]{}));

        int eventId = eventDAO.insertEventAndReturnId(event);
        System.out.println("DEBUG - Inserted eventId: " + eventId);

        if (eventId == -1) {
            System.out.println("DEBUG - Failed to insert event.");
            response.getWriter().println("Failed to store the event in the database.");
            return;
        }

        switch (selectionType) {
            case "class":
                if (selectedClasses != null && selectedClasses.length > 0) {
                    participantDAO.addParticipantsByClass(Arrays.asList(selectedClasses), eventId);
                }
                break;
            case "individual":
                if (selectedICsFromIndividual != null) {
                    for (String ic : selectedICsFromIndividual) {
                        if (ic != null && !ic.trim().isEmpty()) {
                            participantDAO.addParticipantByIC(ic.trim(), eventId);
                        }
                    }
                }
                break;
            case "sport":
                if (sportTeam != null && !sportTeam.isEmpty()) {
                    participantDAO.addParticipantsBySport(sportTeam, eventId);
                }
                break;
            case "uniform":
                if (uniformUnit != null && !uniformUnit.isEmpty()) {
                    participantDAO.addParticipantsByUniform(uniformUnit, eventId);
                }
                break;
        }

        // Google Calendar JSON
        JSONObject eventJson = new JSONObject();
        eventJson.put("summary", title);
        eventJson.put("description", description);
        JSONObject start = new JSONObject();
        start.put("dateTime", startTimeStr);
        start.put("timeZone", timeZone);
        eventJson.put("start", start);
        JSONObject end = new JSONObject();
        end.put("dateTime", endTimeStr);
        end.put("timeZone", timeZone);
        eventJson.put("end", end);

        // Google Calendar API call
        URL url = new URL("https://v1.nocodeapi.com/aqilah/calendar/mtudlycLrYWeEomM/event");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(eventJson.toString().getBytes());
        }

        int responseCode = conn.getResponseCode();
        System.out.println("DEBUG - Google Calendar API response code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            if ("school".equalsIgnoreCase(category)) {
                HttpSession session = request.getSession();
                session.setAttribute("eventId", eventId);
                session.setAttribute("eventTitle", title);
                session.setAttribute("eventStartTime", startTimeStr);
                session.setAttribute("eventEndTime", endTimeStr);
                System.out.println("DEBUG - Redirecting to bookingClass.jsp");
                response.sendRedirect(request.getContextPath() + "/teacher/bookingClass.jsp?success=true&eventId=" + eventId);
            } else {
                System.out.println("DEBUG - Redirecting to eventList.jsp");
                response.sendRedirect(request.getContextPath() + "/teacher/eventList.jsp?success=true&category=" + category);
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                errorResponse.append(line);
            }
            System.out.println("DEBUG - Google Calendar Error: " + errorResponse);

            if ("school".equalsIgnoreCase(category)) {
                System.out.println("DEBUG - Redirecting to bookingClass.jsp (with calendarError)");
                
                response.sendRedirect(request.getContextPath() + "/teacher/bookingClass.jsp?calendarError=true&eventId=" + eventId);
            } else {
                System.out.println("DEBUG - Redirecting to eventList.jsp (with calendarError)");
                response.sendRedirect(request.getContextPath() + "/teacher/eventList.jsp?calendarError=true&category=" + category);
            }
        }
    }
}
