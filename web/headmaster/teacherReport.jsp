<%-- 
    Document   : teacherReport
    Created on : Jun 28, 2025, 1:13:02 AM
    Author     : Lenovo
--%>

<%@page import="dao.TeacherDAO"%>
<%@page import="model.Teacher"%>
<%@page import="java.util.*"%>
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
    <title>Teacher Report</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>

<div class="container mt-5">
    <h3>👩‍🏫 Teacher List by Class</h3>

    <form method="get" class="form-inline mb-4">
        <label class="mr-2">Filter by Class:</label>
        <select name="classFilter" class="form-control mr-2" onchange="this.form.submit()">
            <option value="">-- All Classes --</option>
            <%
                TeacherDAO teacherDAO = new TeacherDAO();
                List<String> assignedClasses = teacherDAO.getAssignedClasses();
                String selectedClass = request.getParameter("classFilter");

                for (String cls : assignedClasses) {
                    String selected = cls.equals(selectedClass) ? "selected" : "";
            %>
            <option value="<%= cls %>" <%= selected %>><%= cls %></option>
            <%
                }
            %>
        </select>
    </form>

    <table class="table table-bordered">
        <thead class="thead-light">
            <tr>
                <th>#</th>
                <th>Teacher Name</th>
                <th>Email</th>
                <th>Contact Number</th>
                <th>Role</th>
                <th>Class Assigned</th>
                <th>Guru Kelas</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Teacher> teachers;

                if (selectedClass != null && !selectedClass.isEmpty()) {
                    teachers = new ArrayList<>();
                    Teacher filteredTeacher = teacherDAO.getTeacherByClass(selectedClass);
                    if (filteredTeacher != null) {
                        teachers.add(filteredTeacher);
                    }
                } else {
                    teachers = teacherDAO.getTeachersByRole("Teacher"); // You can change role filter here
                }

                int i = 1;
                for (Teacher t : teachers) {
            %>
            <tr>
                <td><%= i++ %></td>
                <td><%= t.getName() %></td>
                <td><%= t.getEmail() %></td>
                <td><%= t.getContactNumber() %></td>
                <td><%= t.getRole() %></td>
                <td><%= t.getKelas() != null ? t.getKelas() : "-" %></td>
                <td><%= "Yes".equalsIgnoreCase(t.getIsGuruKelas()) ? "Yes" : "No" %></td>
            </tr>
            <%
                }

                if (teachers.isEmpty()) {
            %>
            <tr>
                <td colspan="7" class="text-center">No teacher found for this class.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>

</body>
</html>
