<%-- 
    Document   : studentReport
    Created on : Jun 28, 2025, 1:13:13 AM
    Author     : Lenovo
--%>

<%@page import="dao.StudentDAO"%>
<%@page import="dao.ParentDAO"%>
<%@page import="model.Student"%>
<%@page import="model.Parent"%>
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
        <title>Student Report</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    </head>
    <body>

        <div class="container mt-5">
            <h3>🧒 Student List by Class</h3>
            <form method="get" class="form-inline mb-4">
                <label class="mr-2">Select Class:</label>
                <select name="selectedClass" class="form-control mr-2" onchange="this.form.submit()">
                    <option value="">-- All Classes --</option>
                    <%
                        // Example: Replace with actual class list from your DB if needed
                        String[] classes = {"1 Makkah", "1 Madinah", "2 Makkah", "2 Madinah", "3 Makkah"};
                        String selectedClass = request.getParameter("selectedClass");

                        for (String c : classes) {
                            String selected = c.equals(selectedClass) ? "selected" : "";
                    %>
                    <option value="<%= c%>" <%= selected%>><%= c%></option>
                    <%
                        }
                    %>
                </select>
            </form>

            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th>#</th>
                        <th>Student Name</th>
                        <th>IC Number</th>
                        <th>Sport Team</th>
                        <th>Uniform Unit</th>
                        <th>Parent Details</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        StudentDAO studentDAO = new StudentDAO();
                        List<Student> students = selectedClass != null && !selectedClass.isEmpty()
                                ? studentDAO.getStudentsByClass(selectedClass) : new ArrayList<Student>();

                        int count = 1;
                        for (Student s : students) {
                            ParentDAO parentDAO = new ParentDAO();
                            Parent parent = parentDAO.getParentByStudentIc(s.getIcNumber());
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= s.getStudentName()%></td>
                        <td><%= s.getIcNumber()%></td>
                        <td><%= s.getSportTeam()%></td>
                        <td><%= s.getUniformUnit()%></td>
                        <td>
                            <% if (parent != null) {%>
                            <button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#parentModal<%= s.getId()%>">
                                View Parent
                            </button>

                            <!-- Modal -->
                            <div class="modal fade" id="parentModal<%= s.getId()%>" tabindex="-1" role="dialog" aria-labelledby="modalLabel<%= s.getId()%>" aria-hidden="true">
                                <div class="modal-dialog" role="document">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="modalLabel<%= s.getId()%>">👨‍👩‍👧 Parent Details</h5>
                                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                        </div>
                                        <div class="modal-body">
                                            <p><strong>Name:</strong> <%= parent.getName()%></p>
                                            <p><strong>Email:</strong> <%= parent.getEmail()%></p>
                                            <p><strong>Phone:</strong> <%= parent.getContactNumber()%></p>
                                            <p><strong>IC Number:</strong> <%= parent.getIcNumber()%></p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <% } else { %>
                            <span class="text-danger">No parent linked</span>
                            <% } %>
                        </td>
                    </tr>
                    <%
                        }
                        if (students.isEmpty()) {
                    %>
                    <tr><td colspan="6" class="text-center">No students found for selected class.</td></tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <!-- Bootstrap JS (for modals) -->
        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>
