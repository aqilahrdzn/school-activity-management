<%@page import="java.util.List"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="util.DBConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="dao.EventDAO"%>
<%@page import="dao.EventParticipantDAO"%>
<%@page import="dao.StudentDAO"%>
<%@page import="model.Event"%>
<%@page import="model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String lang = request.getParameter("lang");
    if (lang != null) {
        session.setAttribute("lang", lang);
    }
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) {
        currentLang = "ms";
    }
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages", new java.util.Locale(currentLang));
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= bundle.getString("event_report") %></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>

<div class="container mt-5">
    <h3><%= bundle.getString("event_report") %></h3>

    <!-- Filter Form -->
    <form class="form-inline mb-4" method="get">
        <label class="mr-2"><%= bundle.getString("filter_by_category") %></label>
        <select name="category" class="form-control mr-3" onchange="this.form.submit()">
            <option value=""><%= bundle.getString("all") %></option>
            <%
                String[] categories = {"School", "External", "Payment"};
                String selectedCat = request.getParameter("category");
                for (String cat : categories) {
                    String selected = cat.equalsIgnoreCase(selectedCat) ? "selected" : "";
            %>
            <option value="<%= cat %>" <%= selected %>><%= cat %></option>
            <%
                }
            %>
        </select>

        <label class="mr-2"><%= bundle.getString("filter_by_month") %>:</label>
        <input type="month" class="form-control" name="filterMonthYear"
               value="<%= request.getParameter("filterMonthYear") != null ? request.getParameter("filterMonthYear") : "" %>"
               onchange="this.form.submit()">
    </form>

    <!-- Event Table -->
    <table class="table table-bordered table-striped">
        <thead class="thead-light">
        <tr>
            <th>#</th>
            <th><%= bundle.getString("event_title") %></th>
            <th><%= bundle.getString("category") %></th>
            <th><%= bundle.getString("start_date") %></th>
            <th><%= bundle.getString("end_date") %></th>
            <th><%= bundle.getString("created_by") %></th>
            <th><%= bundle.getString("payment") %> (RM)</th>
            <th><%= bundle.getString("participant") %></th>
        </tr>
        </thead>
        <tbody>
        <%
            EventDAO eventDAO = new EventDAO();
            EventParticipantDAO participantDAO = new EventParticipantDAO();
            StudentDAO studentDAO = new StudentDAO();

            List<Event> events = eventDAO.getAllEvents();
            String categoryFilter = request.getParameter("category");
            String monthYearFilter = request.getParameter("filterMonthYear");

            int count = 1;
            for (Event ev : events) {
                boolean show = true;

                if (categoryFilter != null && !categoryFilter.isEmpty()
                        && !ev.getCategory().equalsIgnoreCase(categoryFilter)) {
                    show = false;
                }

                if (monthYearFilter != null && !monthYearFilter.isEmpty()) {
                    String eventMonth = ev.getStartTime().substring(0, 7); // yyyy-MM
                    if (!eventMonth.equals(monthYearFilter)) {
                        show = false;
                    }
                }

                if (!show) continue;

                int eventId = -1;
                try {
                    eventId = Integer.parseInt(ev.getId());
                } catch (Exception e) {
                    out.println("<tr><td colspan='8'>Invalid event ID for: " + ev.getTitle() + "</td></tr>");
                    continue;
                }
        %>
        <tr>
            <td><%= count++ %></td>
            <td><%= ev.getTitle() %></td>
            <td><%= ev.getCategory() %></td>
            <td><%= ev.getStartTime() %></td>
            <td><%= ev.getEndTime() %></td>
            <td><%= ev.getCreatedBy() %></td>
            <td><%= ev.getPaymentAmount() > 0 ? String.format("%.2f", ev.getPaymentAmount()) : "-" %></td>
            <td>
                <a href="eventParticipant.jsp?eventId=<%= eventId %>" class="btn btn-info btn-sm"><%= bundle.getString("view_participant") %></a>

                <!-- Modal -->
                <div class="modal fade" id="participantsModal<%= eventId %>" tabindex="-1" role="dialog" aria-labelledby="modalLabel<%= eventId %>" aria-hidden="true">
                    <div class="modal-dialog modal-lg" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="modalLabel<%= eventId %>"><%= bundle.getString("event_participant_list") %> <%= ev.getTitle() %></h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <table class="table table-sm table-bordered">
                                    <thead>
                                    <tr>
                                        <th><%= bundle.getString("name") %></th>
                                        <th><%= bundle.getString("ic_number") %></th>
                                        <th><%= bundle.getString("approval_status") %></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        List<String> studentICs = participantDAO.getICsByEventId(eventId);
                                        for (String ic : studentICs) {
                                            Student s = studentDAO.getStudentByIC(ic);
                                            String status = "-";

                                            try (Connection conn = DBConfig.getConnection();
                                                 PreparedStatement ps = conn.prepareStatement(
                                                         "SELECT status FROM parent_approval WHERE student_ic = ? AND event_id = ? LIMIT 1")) {
                                                ps.setString(1, ic);
                                                ps.setInt(2, eventId);
                                                ResultSet rs = ps.executeQuery();
                                                if (rs.next()) {
                                                    status = rs.getString("status");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                    %>
                                    <tr>
                                        <td><%= s != null ? s.getStudentName() : "N/A" %></td>
                                        <td><%= ic %></td>
                                        <td><%= status %></td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
        <%
            }
            if (count == 1) {
        %>
        <tr><td colspan="8" class="text-center">No events found.</td></tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>

<!-- Bootstrap Modal JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
