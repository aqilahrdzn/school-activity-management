<%-- 
    Document   : parentReport
    Created on : Jun 28, 2025, 1:31:36 AM
    Author     : Lenovo
--%>

<%@page import="dao.ParentDAO"%>
<%@page import="dao.StudentDAO"%>
<%@page import="model.Parent"%>
<%@page import="model.Student"%>
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
        <title>Parent Report</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    </head>
    <body>

        <div class="container mt-5">
            <h3><%= bundle.getString("event_report") %></h3>

            <form method="get" class="form-inline mb-4">
                <label class="mr-2"><%= bundle.getString("select_class") %></label>
                <select name="classFilter" class="form-control mr-2" onchange="this.form.submit()">
                    <option value=""><%= bundle.getString("event_report") %></option>
                    <%
                        String[] classes = {"1 Makkah", "1 Madinah", "2 Makkah", "2 Madinah", "3 Makkah"};
                        String classFilter = request.getParameter("classFilter");

                        for (String c : classes) {
                            String selected = c.equals(classFilter) ? "selected" : "";
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
                        <th><%= bundle.getString("parent_name") %></th>
                        <th><%= bundle.getString("email") %></th>
                        <th><%= bundle.getString("contact_number") %></th>
                        <th><%= bundle.getString("ic_number") %></th>
                        <th><%= bundle.getString("children") %></th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        ParentDAO parentDAO = new ParentDAO();
                        StudentDAO studentDAO = new StudentDAO();

                        List<Student> allStudents = new ArrayList<>();
                        if (classFilter != null && !classFilter.isEmpty()) {
                            allStudents = studentDAO.getStudentsByClass(classFilter);
                        } else {
                            // fallback if you want to include all students
                            allStudents = new ArrayList<>();
                        }

                        Map<Integer, List<Student>> parentToChildren = new LinkedHashMap<>();
                        Map<Integer, Parent> parentMap = new HashMap<>();

                        for (Student s : allStudents) {
                            Parent p = parentDAO.getParentByStudentIc(s.getIcNumber());
                            if (p != null) {
                                parentMap.put(p.getId(), p);
                                if (!parentToChildren.containsKey(p.getId())) {
                                    parentToChildren.put(p.getId(), new ArrayList<Student>());
                                }
                                parentToChildren.get(p.getId()).add(s);

                            }
                        }

                        int count = 1;
                        for (Map.Entry<Integer, Parent> entry : parentMap.entrySet()) {
                            Parent p = entry.getValue();
                            List<Student> children = parentToChildren.get(p.getId());
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= p.getName()%></td>
                        <td><%= p.getEmail()%></td>
                        <td><%= p.getContactNumber()%></td>
                        <td><%= p.getIcNumber()%></td>
                        <td>
                            <ul>
                                <% for (Student child : children) {%>
                                <li><%= child.getStudentName()%></li>
                                    <% } %>
                            </ul>
                        </td>
                    </tr>
                    <%
                        }

                        if (parentMap.isEmpty()) {
                    %>
                    <tr><td colspan="6" class="text-center"><%= bundle.getString("no_parent") %>.</td></tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

    </body>
</html>
