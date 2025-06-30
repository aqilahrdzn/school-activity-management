<%@ page import="java.sql.*" %>
<%@ page import="util.DBConfig" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%
    String lang = request.getParameter("lang");
    if (lang != null) {
        session.setAttribute("lang", lang);
    }
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) currentLang = "ms"; // Default: BM

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages", new java.util.Locale(currentLang));
%>
<%
    String eventId = request.getParameter("eventId");
    if (eventId == null || eventId.isEmpty()) {
        response.sendRedirect("eventList.jsp");
        return;
    }

    String eventTitle = "", eventStartTime = "", eventEndTime = "", eventDescription = "";
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
                if (filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
                    imagePaths.add(request.getContextPath() + "/" + filePath);
                }
                if (eventDescription.isEmpty()) {
                    eventDescription = (fileDescription == null) ? "" : fileDescription;
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
    <title>Event Report - <%= eventTitle %></title>
    <style>
    body {
        font-family: "Segoe UI", Tahoma, sans-serif;
        width: 100%;
        max-width: 800px;
        margin: 0 auto;
        padding: 20px;
        line-height: 1.4;
        color: #333;
        font-size: 11pt;
    }

    .header {
        text-align: center;
        border-bottom: 1px solid #444;
        padding-bottom: 10px;
        margin-bottom: 20px;
    }

    .header img {
        max-width: 120px; /* Enlarged logo */
        margin: 0 20px;
    }

    .event-title {
        font-size: 22pt;
        margin: 10px 0 5px;
    }

    .event-info {
        background-color: #f9f9f9;
        padding: 8px 12px;
        border-left: 3px solid #007bff;
        margin-bottom: 15px;
        font-size: 10pt;
    }

    .section-title {
        font-size: 14pt;
        margin-bottom: 8px;
        border-bottom: 1px solid #ccc;
        padding-bottom: 3px;
    }

    .description {
        white-space: pre-line;
        margin-bottom: 20px;
    }

    .image-section {
        margin-bottom: 10px;
        text-align: center;
    }

    .report-image {
        width: 100%;
        max-width: 330px; /* Larger image */
        display: inline-block;
        margin: 10px;
        border: 1px solid #ccc;
        padding: 4px;
        vertical-align: top;
        page-break-inside: avoid;
    }

    .no-image {
        font-style: italic;
        color: #777;
        text-align: center;
    }

    @media print {
        @page {
            size: A4;
            margin: 10mm;
        }

        body {
            font-size: 10pt;
            padding: 0;
        }

        .header img {
            max-width: 100px;
        }

        .event-title {
            font-size: 20pt;
        }

        .report-image {
            max-width: 300px;
            margin: 8px;
        }
    }
</style>


</head>
<body>

<div class="header">
    <div>
        <img src="../assets/images/SKKJ.png" alt="School Logo">
        <img src="../assets/images/kementerian (2).png" alt="Kementerian Logo">
    </div>
    <h1 class="event-title"><%= eventTitle %></h1>
</div>

<div class="event-info">
    <strong><%= bundle.getString("event_time_label") %>:</strong><br>
    <%= eventStartTime %> &nbsp; to &nbsp; <%= eventEndTime %>
</div>

<div>
    <div class="section-title"><%= bundle.getString("event_description_section_title") %></div>
    <p class="description"><%= eventDescription %></p>
</div>

<div class="image-section">
    <div class="section-title"><%= bundle.getString("event_pictures_section_title") %></div>
    <% if (!imagePaths.isEmpty()) { 
        for (String path : imagePaths) { %>
            <img src="<%= path %>" class="report-image" alt="Event Image">
    <%  }
    } else { %>
        <p class="no-image"><%= bundle.getString("no_images_uploaded") %></p>
    <% } %>
</div>

</body>
</html>
