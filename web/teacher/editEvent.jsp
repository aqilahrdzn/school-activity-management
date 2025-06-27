<%-- 
    Document   : editEvent
    Created on : Jun 26, 2025, 7:47:20 PM
    Author     : Lenovo
--%>

<%@ page import="model.Event" %>
<%@ page import="dao.EventDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String eventIdParam = request.getParameter("eventId");
    Event event = null;
    if (eventIdParam != null) {
        int eventId = Integer.parseInt(eventIdParam);
        EventDAO dao = new EventDAO();
        event = dao.getEventById(eventId);
    }
    if (event == null) {
%>
    <div class="alert alert-danger">Event not found.</div>
<%
    return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Event</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2>Edit Event</h2>
    <form method="post" action="<%= request.getContextPath() %>/EventController">
        <input type="hidden" name="action" value="update" />
        <input type="hidden" name="eventId" value="<%= event.getId() %>" />

        <div class="mb-3">
            <label class="form-label">Title</label>
            <input type="text" class="form-control" name="title" value="<%= event.getTitle() %>" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea class="form-control" name="description" rows="3"><%= event.getDescription() %></textarea>
        </div>
        <div class="mb-3">
            <label class="form-label">Start Time</label>
            <input type="datetime-local" class="form-control" name="startTime" value="<%= event.getStartTime().replace(" ", "T") %>" required />
        </div>
        <div class="mb-3">
            <label class="form-label">End Time</label>
            <input type="datetime-local" class="form-control" name="endTime" value="<%= event.getEndTime().replace(" ", "T") %>" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Time Zone</label>
            <input type="text" class="form-control" name="timeZone" value="<%= event.getTimeZone() %>" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Category</label>
            <input type="text" class="form-control" name="event-category" value="<%= event.getCategory() %>" required />
        </div>
        <div class="mb-3">
            <label class="form-label">Payment Amount (if applicable)</label>
            <input type="number" step="0.01" class="form-control" name="paymentAmount" value="<%= event.getPaymentAmount() %>" />
        </div>

        <button type="submit" class="btn btn-primary">Update Event</button>
        <a href="eventList.jsp" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html>
