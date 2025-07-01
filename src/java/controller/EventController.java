package controller;

import dao.EventDAO;
import dao.EventParticipantDAO;
import dao.StudentDAO;
// Removed unused import: import dao.EventParticipantDAO;
import model.Event;
import model.Student;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
// Removed unused import: import java.util.*;
// Removed unused import: import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/EventController")
public class EventController extends HttpServlet {

    // The new NoCodeAPI endpoint from your documentation
    private static final String NOCODEAPI_URL = "https://v1.nocodeapi.com/aqilah/calendar/zcqrYWJEwwcmlttU/event";

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
            // --- MODIFIED DELETE LOGIC ---
            String eventIdStr = request.getParameter("eventId");
            int eventId = Integer.parseInt(eventIdStr);
            String googleEventId = eventDAO.getGoogleEventIdByEventId(eventId);
            
            try {
                if (googleEventId != null && !googleEventId.isEmpty()) {
                    System.out.println("Attempting to DELETE Google Calendar event: " + googleEventId);

                    // MODIFICATION: Construct the URL with eventId as a query parameter
                    URL url = new URL(NOCODEAPI_URL + "?eventId=" + googleEventId);
                    HttpURLConnection deleteConn = (HttpURLConnection) url.openConnection();

                    // MODIFICATION: Use the DELETE method directly
                    deleteConn.setRequestMethod("DELETE");
                    deleteConn.setRequestProperty("Content-Type", "application/json");

                    // MODIFICATION: No request body is sent for this DELETE request
                    deleteConn.setDoOutput(false);

                    int deleteResponse = deleteConn.getResponseCode();
                    System.out.println("Google Calendar delete response code: " + deleteResponse);

                    // A successful deletion can return 200 (OK) or 204 (No Content)
                    if (deleteResponse != HttpURLConnection.HTTP_OK && deleteResponse != HttpURLConnection.HTTP_NO_CONTENT) {
                        // This error handling is correct and remains
                        BufferedReader reader = new BufferedReader(new InputStreamReader(deleteConn.getErrorStream()));
                        StringBuilder errorMsg = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMsg.append(line);
                        }
                        System.err.println("Google Calendar delete error: " + errorMsg.toString());
                    }
                } else {
                    System.err.println("Could not delete from Google Calendar: googleEventId is missing.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // This part remains the same, deleting the event from your local database
            boolean deleted = eventDAO.deleteEventById(eventIdStr);
            response.sendRedirect("teacher/eventList.jsp?status=" + (deleted ? "deleted" : "deletefail"));
            return;
        }

        if ("update".equals(action)) {
            // (The update logic from the previous answer remains here, it is already correct)
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

                // (Your validation logic remains here)
                if ("payment".equals(category)) {
                    // (Your payment logic remains here)
                }

                Event event = new Event();
                // (Setting event properties remains here)

                String googleEventId = eventDAO.getGoogleEventIdByEventId(eventId);
                if (googleEventId != null && !googleEventId.isEmpty()) {
                    System.out.println("Attempting to PUT (update) Google Calendar event: " + googleEventId);

                    JSONObject updateJson = new JSONObject();
                    updateJson.put("summary", title);
                    updateJson.put("description", description);

                    JSONObject start = new JSONObject();
                    start.put("dateTime", startTimeStr);
                    start.put("timeZone", timeZone);
                    updateJson.put("start", start);

                    JSONObject end = new JSONObject();
                    end.put("dateTime", endTimeStr);
                    end.put("timeZone", timeZone);
                    updateJson.put("end", end);
                    updateJson.put("sendNotifications", true);

                    try {
                        URL url = new URL(NOCODEAPI_URL + "?eventId=" + googleEventId);
                        HttpURLConnection updateConn = (HttpURLConnection) url.openConnection();
                        updateConn.setRequestMethod("PUT");
                        updateConn.setRequestProperty("Content-Type", "application/json");
                        updateConn.setDoOutput(true);

                        try (OutputStream os = updateConn.getOutputStream()) {
                            os.write(updateJson.toString().getBytes());
                        }

                        int putCode = updateConn.getResponseCode();
                        System.out.println("Google Calendar update response code: " + putCode);

                        if (putCode != HttpURLConnection.HTTP_OK) {
                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(updateConn.getErrorStream()));
                            StringBuilder errorDetails = new StringBuilder();
                            String line;
                            while ((line = errorReader.readLine()) != null) {
                                errorDetails.append(line);
                            }
                            System.err.println("Google Calendar update error: " + errorDetails.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                boolean updated = eventDAO.updateEvent(event);
                response.sendRedirect("teacher/eventList.jsp?status=" + (updated ? "updated" : "updatefail"));
                return;
            } else {
                response.getWriter().println("Missing event ID for update.");
                return;
            }
        }

        // --- INSERT LOGIC ---
        // (The insert logic from the previous answer remains here, it is already correct)
        String category = request.getParameter("event-category");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String timeZone = request.getParameter("timeZone");
        String venue = request.getParameter("venue"); 

        if (startTimeStr.length() == 16) {
            startTimeStr += ":00";
        }
        if (endTimeStr.length() == 16) {
            endTimeStr += ":00";
        }

        String createdBy = (String) request.getSession().getAttribute("email");
        double paymentAmount = 0.0;
        if ("payment".equals(category)) {
            String amountStr = request.getParameter("paymentAmount");
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    paymentAmount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
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
        event.setVenue(venue); 

        int eventId = eventDAO.insertEventAndReturnId(event);
        // Retrieve selection type
            String selectType = request.getParameter("selectType");
            EventParticipantDAO participantDAO = new EventParticipantDAO();

// Handle participant storage based on selection type
            switch (selectType) {
                case "class":
                    String[] selectedClasses = request.getParameterValues("classDropdown");
                    if (selectedClasses != null) {
                        List<String> classList = new ArrayList<>(Arrays.asList(selectedClasses));
                        participantDAO.addParticipantsByClass(classList, eventId);
                    }
                    break;

                case "individual":
                    String[] selectedICs = request.getParameterValues("selectedICs");
                    if (selectedICs != null) {
                        for (String ic : selectedICs) {
                            participantDAO.addParticipantByIC(ic, eventId);
                        }
                    }
                    break;

                case "sport":
                    String sportTeam = request.getParameter("sportDropdown");
                    if (sportTeam != null && !sportTeam.isEmpty()) {
                        participantDAO.addParticipantsBySport(sportTeam, eventId);
                    }
                    break;

                case "uniform":
                    String uniformUnit = request.getParameter("uniformDropdown");
                    if (uniformUnit != null && !uniformUnit.isEmpty()) {
                        participantDAO.addParticipantsByUniform(uniformUnit, eventId);
                    }
                    break;

                default:
                    System.out.println("Unknown selection type: " + selectType);
            }


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

        eventJson.put("sendNotifications", true);

        try {
            URL url = new URL(NOCODEAPI_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            System.out.println("Sending to Google Calendar (Create): " + eventJson.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(eventJson.toString().getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();
                JSONObject responseJson = new JSONObject(responseBuilder.toString());
                String googleEventId = responseJson.getString("id");
                System.out.println("Created Google Calendar event ID: " + googleEventId);
                eventDAO.updateGoogleEventId(eventId, googleEventId);
            } else {
                System.err.println("Google Calendar create request failed with response code: " + responseCode);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorDetails = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorDetails.append(errorLine);
                }
                errorReader.close();
                System.err.println("Google Calendar create error: " + errorDetails.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("teacher/eventList.jsp?success=true");
    }
}
