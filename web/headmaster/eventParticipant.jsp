<%-- 
    Document   : eventParticipant
    Created on : Jun 28, 2025, 2:24:34 AM
    Author     : Lenovo
--%>

<%@page import="java.util.List"%>
<%@page import="java.sql.*"%>
<%@page import="util.DBConfig"%>
<%@page import="model.Student"%>
<%@page import="dao.StudentDAO"%>
<%@page import="dao.EventParticipantDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Event Participants</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    </head>
    <body>
        <div class="container mt-4">
            <h3>👥 Participants List</h3>
            <%
                String eventIdParam = request.getParameter("eventId");
                int eventId = -1;
                try {
                    eventId = Integer.parseInt(eventIdParam);
                } catch (Exception e) {
                    out.println("<p class='text-danger'>Invalid event ID</p>");
                }

                if (eventId != -1) {
                    EventParticipantDAO participantDAO = new EventParticipantDAO();
                    StudentDAO studentDAO = new StudentDAO();
                    List<String> studentICs = participantDAO.getICsByEventId(eventId);
            %>

            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th>Name</th>
                        <th>IC Number</th>
                        <th>Approval Status</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (String ic : studentICs) {
                            Student s = studentDAO.getStudentByIC(ic);
                            String status = "-";

                            try (Connection conn = DBConfig.getConnection()) {
                                // Step 1: Get parent_id from student table
                                int parentId = -1;
                                try (PreparedStatement ps = conn.prepareStatement("SELECT parent_id FROM student WHERE ic_number = ?")) {
                                    ps.setString(1, ic);
                                    ResultSet rs = ps.executeQuery();
                                    if (rs.next()) {
                                        parentId = rs.getInt("parent_id");
                                    }
                                }

                                // Step 2: Get approval status from parent_approval
                                if (parentId != -1) {
                                    try (PreparedStatement ps2 = conn.prepareStatement("SELECT status FROM parent_approval WHERE parent_id = ? AND event_id = ? LIMIT 1")) {
                                        ps2.setInt(1, parentId);
                                        ps2.setInt(2, eventId);
                                        ResultSet rs2 = ps2.executeQuery();
                                        if (rs2.next()) {
                                            status = rs2.getString("status");
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    %>
                    <tr>
                        <td><%= s != null ? s.getStudentName() : "N/A"%></td>
                        <td><%= ic%></td>
                        <td><%= status%></td>
                    </tr>
                    <%
                        }
                    %>

                </tbody>
            </table>
            <a href="eventReport.jsp" class="btn btn-secondary">← Back to Event Report</a>
            <%
                }
            %>
        </div>
    </body>
</html>
