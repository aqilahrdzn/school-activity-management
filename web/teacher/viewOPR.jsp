<%@ page import="java.sql.*" %>
<%@ page import="util.DBConfig" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%
    String eventId = request.getParameter("eventId");
    if (eventId == null || eventId.isEmpty()) {
        response.sendRedirect("eventList.jsp"); // Redirect if no event ID is provided
        return;
    }

    String eventTitle = "";
    String eventStartTime = "";
    String eventEndTime = "";
    String eventDescription = "";
    List<String> imagePaths = new ArrayList<>();

    try (Connection connection = DBConfig.getConnection();
         PreparedStatement eventStmt = connection.prepareStatement("SELECT title, start_time, end_time FROM events WHERE id = ?");
         PreparedStatement uploadsStmt = connection.prepareStatement("SELECT file_path, description FROM event_uploads WHERE event_id = ?")) {

        eventStmt.setInt(1, Integer.parseInt(eventId));
        try (ResultSet eventRs = eventStmt.executeQuery()) {
            if (eventRs.next()) {
                eventTitle = eventRs.getString("title");
                eventStartTime = eventRs.getString("start_time");
                eventEndTime = eventRs.getString("end_time");
            } else {
                out.println("Event not found.");
                return;
            }
        }

        uploadsStmt.setInt(1, Integer.parseInt(eventId));
        try (ResultSet uploadsRs = uploadsStmt.executeQuery()) {
            while (uploadsRs.next()) {
                String filePath = uploadsRs.getString("file_path");
                String fileDescription = uploadsRs.getString("description");
                // For simplicity, we'll treat all uploaded files as images for this basic report.
                // You might want to handle different file types (PDFs) differently.
                if (filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
                    imagePaths.add(request.getContextPath() + "/" + filePath);
                }
                if (eventDescription.isEmpty()) {
                    eventDescription = (fileDescription == null) ? "" : fileDescription; // Use the first description found
                }
            }
        }

    } catch (SQLException e) {
        out.println("Error fetching event details: " + e.getMessage());
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>One Page Report - <%= eventTitle %></title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .header {
            text-align: center;
            margin-bottom: 20px;
        }
        .logos {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        .logo {
            max-width: 150px;
            height: auto;
        }
        .event-title {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .event-date {
            margin-bottom: 10px;
        }
        .event-description {
            margin-bottom: 15px;
            white-space: pre-line; /* Preserve line breaks */
        }
        .image-gallery {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .report-image {
            max-width: 300px;
            height: auto;
            border: 1px solid #ccc;
            padding: 5px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="logos">
            <img src="../assets/images/SKKJ.png" alt="School Logo" class="logo">
            <img src="../assets/images/kementerian (2).png" alt="Kementerian Logo" class="logo">
        </div>
        <h1 class="event-title"><%= eventTitle %></h1>
        <p class="event-date">Date & Time: <%= eventStartTime %> - <%= eventEndTime %></p>
    </div>

    <div>
        <h3>Description:</h3>
        <p class="event-description"><%= eventDescription %></p>
    </div>

    <% if (!imagePaths.isEmpty()) { %>
    <div>
        <h3>Event Pictures:</h3>
        <div class="image-gallery">
            <% for (String imagePath : imagePaths) { %>
                <img src="<%= imagePath %>" alt="Event Picture" class="report-image">
            <% } %>
        </div>
    </div>
    <% } else { %>
        <p>No pictures uploaded for this event.</p>
    <% } %>

</body>
</html>