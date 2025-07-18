<%-- 
    Document   : parentApprovals
    Created on : Jun 2, 2025, 11:54:53 PM
    Author     : Lenovo
--%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="model.Teacher"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="util.DBConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String lang = request.getParameter("lang");
    if (lang != null) {
        session.setAttribute("lang", lang);
    }
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) currentLang = "ms"; // Default: BM

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages", new java.util.Locale(currentLang));
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>School Activity Management System</title>
        <!-- plugins:css -->
        <link rel="stylesheet" href="../assets/vendors/mdi/css/materialdesignicons.min.css">
        <link rel="stylesheet" href="../assets/vendors/ti-icons/css/themify-icons.css">
        <link rel="stylesheet" href="../assets/vendors/css/vendor.bundle.base.css">
        <link rel="stylesheet" href="../assets/vendors/font-awesome/css/font-awesome.min.css">
        <!-- endinject -->
        <!-- Plugin css for this page -->
        <link rel="stylesheet" href="../assets/vendors/select2/select2.min.css">
        <link rel="stylesheet" href="../assets/vendors/select2-bootstrap-theme/select2-bootstrap.min.css">
        <!-- End plugin css for this page -->
        <!-- inject:css -->
        <!-- endinject -->
        <!-- Layout styles -->
        <link rel="stylesheet" href="../assets/css/style.css">
        <!-- End layout styles -->
        <link rel="shortcut icon" href="../assets/images/favicon.png" />
    </head>
    <body>

        <%
    String email = (String) session.getAttribute("email");

    TeacherDAO teacherDAO = new TeacherDAO();
    Teacher teacher = null;

    if (email != null) {
        teacher = teacherDAO.getTeacherDetails(email);
    }

    if (teacher == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    int totalTeachers = 0;
    int totalStudents = 0;

    try (Connection conn = DBConfig.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM teachers"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalTeachers = rs.getInt("total");
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM student"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalStudents = rs.getInt("total");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

   
    String eventId = request.getParameter("eventId");

    if (eventId == null || eventId.trim().isEmpty()) {
        out.println("<p style='color:red;'>Error: Missing or invalid event ID.</p>");
        return;
    }

    List<Map<String, String>> approvalList = new ArrayList<>();

    String approvalQuery = "SELECT p.id AS parent_id, p.name AS parent_name, p.email, p.contact_number, p.ic_number AS parent_ic, "
            + "s.student_name, s.class, s.ic_number AS student_ic, "
            + "pa.status, pa.reason, pa.approved_at, pa.resit_file "
            + "FROM parent_approval pa "
            + "JOIN parent p ON pa.parent_id = p.id "
            + "JOIN student s ON s.parent_id = p.id "
            + "WHERE pa.event_id = ?";
    try (Connection connection = DBConfig.getConnection(); PreparedStatement stmt = connection.prepareStatement(approvalQuery)) {
        stmt.setInt(1, Integer.parseInt(eventId));
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("parent_id", rs.getString("parent_id"));
                row.put("parent_name", rs.getString("parent_name"));
                row.put("email", rs.getString("email"));
                row.put("contact_number", rs.getString("contact_number"));
                row.put("parent_ic", rs.getString("parent_ic"));
                row.put("student_name", rs.getString("student_name"));
                row.put("student_ic", rs.getString("student_ic"));
                row.put("class", rs.getString("class"));
                row.put("status", rs.getString("status"));
                row.put("reason", rs.getString("reason"));
                row.put("approved_at", rs.getString("approved_at"));
                row.put("resit_file", rs.getString("resit_file"));
                row.put("event_id", eventId);

                approvalList.add(row);
            }
        }
    } catch (SQLException e) {
        request.setAttribute("error", "Error retrieving approval list: " + e.getMessage());
    }
        %>
        <div class="container-scroller">
            <!-- partial:../../partials/_navbar.html -->
            <nav class="navbar default-layout-navbar col-lg-12 col-12 p-0 fixed-top d-flex flex-row">
                <div class="text-center navbar-brand-wrapper d-flex align-items-center justify-content-start">
                    <a class="navbar-brand brand-logo" href="teacherdashboard.jsp"><img src="../assets/images/skkj_logo.jpg" width="1000" height="50" alt="logo" /></a>
                    <a class="navbar-brand brand-logo-mini" href="index.jsp"><img src="../assets/images/logo-mini.svg" alt="logo" /></a>
                </div>
                <div class="navbar-menu-wrapper d-flex align-items-stretch">
                    <button class="navbar-toggler navbar-toggler align-self-center" type="button" data-toggle="minimize">
                        <span class="mdi mdi-menu"></span>
                    </button>
                    <div class="search-field d-none d-md-block">
                        <form class="d-flex align-items-center h-100" action="#">
                            <div class="input-group">
                                <div class="input-group-prepend bg-transparent">
                                    <i class="input-group-text border-0 mdi mdi-magnify"></i>
                                </div>
                                <input type="text" class="form-control bg-transparent border-0" placeholder="Search projects">
                            </div>
                        </form>
                    </div>
                    <ul class="navbar-nav navbar-nav-right">
                        <li class="nav-item nav-profile dropdown">
                            <a class="nav-link dropdown-toggle" id="profileDropdown" href="#" data-bs-toggle="dropdown" aria-expanded="false">
                                <div class="nav-profile-text">
                                    <p class="mb-1 text-black"><%= teacher.getName()%></p>
                                </div>
                            </a>
                            <div class="dropdown-menu navbar-dropdown" aria-labelledby="profileDropdown">
                                <a class="dropdown-item" href="#">
                                    <i class="mdi mdi-cached me-2 text-success"></i> Activity Log </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="../login.jsp">
                                    <i class="mdi mdi-logout me-2 text-primary"></i> Signout </a>

                            </div>
                        </li>

                        <li class="nav-item dropdown">
                            <a class="nav-link count-indicator dropdown-toggle" id="messageDropdown" href="#" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="mdi mdi-email-outline"></i>
                                <span class="count-symbol bg-warning"></span>
                            </a>
                            <div class="dropdown-menu dropdown-menu-end navbar-dropdown preview-list" aria-labelledby="messageDropdown">
                                <h6 class="p-3 mb-0">Messages</h6>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <img src="../../assets/images/faces/face4.jpg" alt="image" class="profile-pic">
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject ellipsis mb-1 font-weight-normal">Mark send you a message</h6>
                                        <p class="text-gray mb-0"> 1 Minutes ago </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <img src="../../assets/images/faces/face2.jpg" alt="image" class="profile-pic">
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject ellipsis mb-1 font-weight-normal">Cregh send you a message</h6>
                                        <p class="text-gray mb-0"> 15 Minutes ago </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <img src="../../assets/images/faces/face3.jpg" alt="image" class="profile-pic">
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject ellipsis mb-1 font-weight-normal">Profile picture updated</h6>
                                        <p class="text-gray mb-0"> 18 Minutes ago </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <h6 class="p-3 mb-0 text-center">4 new messages</h6>
                            </div>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link count-indicator dropdown-toggle" id="notificationDropdown" href="#" data-bs-toggle="dropdown">
                                <i class="mdi mdi-bell-outline"></i>
                                <span class="count-symbol bg-danger"></span>
                            </a>
                            <div class="dropdown-menu dropdown-menu-end navbar-dropdown preview-list" aria-labelledby="notificationDropdown">
                                <h6 class="p-3 mb-0">Notifications</h6>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <div class="preview-icon bg-success">
                                            <i class="mdi mdi-calendar"></i>
                                        </div>
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject font-weight-normal mb-1">Event today</h6>
                                        <p class="text-gray ellipsis mb-0"> Just a reminder that you have an event today </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <div class="preview-icon bg-warning">
                                            <i class="mdi mdi-cog"></i>
                                        </div>
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject font-weight-normal mb-1">Settings</h6>
                                        <p class="text-gray ellipsis mb-0"> Update dashboard </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <div class="preview-icon bg-info">
                                            <i class="mdi mdi-link-variant"></i>
                                        </div>
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject font-weight-normal mb-1">Launch Admin</h6>
                                        <p class="text-gray ellipsis mb-0"> New admin wow! </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <h6 class="p-3 mb-0 text-center">See all notifications</h6>
                            </div>
                        </li>
                        <li class="nav-item nav-logout d-none d-lg-block">
                            <a class="nav-link" href="../login.jsp">
                                <i class="mdi mdi-power"></i>
                            </a>
                        </li>

                    </ul>
                    <button class="navbar-toggler navbar-toggler-right d-lg-none align-self-center" type="button" data-toggle="offcanvas">
                        <span class="mdi mdi-menu"></span>
                    </button>
                </div>
            </nav>
            <!-- partial -->
            <div class="container-fluid page-body-wrapper">
                <!-- partial:../../partials/_sidebar.html -->
                <nav class="sidebar sidebar-offcanvas" id="sidebar">
                    <ul class="nav">
                        <li class="nav-item nav-profile">
                            <a href="#" class="nav-link">
                                <div class="nav-profile-image">
                                    <img src="<%= (teacher != null && teacher.getProfilePicture() != null) ? "../profile_pics/" + teacher.getProfilePicture() : "../assets/images/faces/default.jpg"%>" alt="profile" />

                                    <span class="login-status online"></span>
                                </div>

                                <div class="nav-profile-text d-flex flex-column">
                                    <span class="font-weight-bold mb-2"><%= teacher.getName()%></span>
                                    <span class="text-secondary text-small"><%= teacher.getRole()%></span>
                                </div>
                                <i class="mdi mdi-bookmark-check text-success nav-profile-badge"></i>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="teacherdashboard.jsp">
                                <span class="menu-title"><%= bundle.getString("dashboard")%></span>
                                <i class="mdi mdi-home menu-icon"></i>
                            </a>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#forms" aria-expanded="false" aria-controls="forms">
                                <span class="menu-title"><%= bundle.getString("forms")%></span>
                                <i class="mdi mdi-format-list-bulleted menu-icon"></i>
                            </a>
                            <div class="collapse" id="forms">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="studentRegistration.jsp"><%= bundle.getString("student_register_nav")%></a>
                                        <a class="nav-link" href="createEvent.jsp"><%= bundle.getString("create_event_nav")%></a>
                                        <a class="nav-link" href="updateAccTc.jsp"><%= bundle.getString("update_account_nav")%></a>

                                    </li>
                                </ul>
                            </div>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#charts" aria-expanded="false" aria-controls="charts">
                                <span class="menu-title"><%= bundle.getString("list")%></span>
                                <i class="mdi mdi-chart-bar menu-icon"></i>
                            </a>
                            <div class="collapse" id="charts">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="studentList.jsp"><%= bundle.getString("student_list")%></a>
                                        <a class="nav-link" href="eventList.jsp"><%= bundle.getString("event_list")%></a>
                                    </li>
                                </ul>
                            </div>
                        </li>

                    </ul>
                </nav>
                <!-- partial -->
                <div class="main-panel">
                    <div class="content-wrapper">
                        <div class="page-header">
                            <h3 class="page-title"> Form elements </h3>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                    <li class="breadcrumb-item"><a href="#">Forms</a></li>
                                    <li class="breadcrumb-item active" aria-current="page">Form elements</li>
                                </ol>
                            </nav>
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-12 grid-margin">
                            <div class="card">
                                <div class="card-body">
                                    <h4 class="card-title"><%= bundle.getString("parent_approval_list") %></h4>
                                    <div class="table-responsive">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th><%= bundle.getString("student_name") %></th>
                                                    <th><%= bundle.getString("student_ic") %></th>
                                                    <th><%= bundle.getString("classroom_name") %></th>
                                                    <th><%= bundle.getString("status") %></th>
                                                    <th><%= bundle.getString("reason") %></th>
                                                    <th><%= bundle.getString("approved_at") %></th>
                                                    <th><%= bundle.getString("disqualified") %></th>
                                                    <th><%= bundle.getString("resit_file") %></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    if (request.getAttribute("error") != null) {
                                                %>
                                                <tr><td colspan="10"><%= request.getAttribute("error") %></td></tr>
                                                    <%
                                                        } else if (!approvalList.isEmpty()) {
                                                            for (Map<String, String> row : approvalList) {
                                                                String status = row.get("status");
                                                                String disqualified = row.get("disqualified");
                                                    %>
                                                <tr>
                                                    <td><%= row.get("student_name") %></td>
                                                    <td><%= row.get("student_ic") %></td>
                                                    <td><%= row.get("class") %></td>
                                                    <td>
                                                        <% if ("Approved".equalsIgnoreCase(status)) { %>
                                                        <span class="badge bg-success"><%= bundle.getString("approved") %></span>
                                                        <% } else if ("Rejected".equalsIgnoreCase(status)) { %>
                                                        <span class="badge bg-warning text-dark"><%= bundle.getString("rejected") %></span>
                                                        <% } else if ("Disqualified".equalsIgnoreCase(status) || "true".equalsIgnoreCase(disqualified)) { %>
                                                        <span class="badge bg-danger"><%= bundle.getString("disqualified") %></span>
                                                        <% } else { %>
                                                        <span class="badge bg-secondary"><%= bundle.getString("pending") %></span>
                                                        <% } %>
                                                    </td>
                                                    <td><%= row.get("reason") != null ? row.get("reason") : "-" %></td>
                                                    <td><%= row.get("approved_at") != null ? row.get("approved_at") : "-" %></td>
                                                    <td><%= "true".equalsIgnoreCase(disqualified) ? "Yes" : "No" %></td>
                                                    <td>
                                                        <%
                                                            String resitPath = row.get("resit_file");
                                                            if (resitPath != null && !resitPath.trim().isEmpty()) {
                                                        %>
                                                        <a href="<%= request.getContextPath() %>/ViewResitServlet?event_id=<%= row.get("event_id") %>&parent_id=<%= row.get("parent_id") %>" target="_blank"><%= bundle.getString("view_resit") %></a>
                                                        <%
                                                            } else {
                                                        %>
                                                        <%= bundle.getString("no_file") %>
                                                        <%
                                                            }
                                                        %>
                                                    </td>
                                                </tr>
                                                <%
                                                        }
                                                    } else {
                                                %>
                                                <tr><td colspan="10"><%= bundle.getString("no_approval_found") %></td></tr>
                                                <%
                                                    }
                                                %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- content-wrapper ends -->
                    <!-- partial:../../partials/_footer.html -->
                    <footer class="footer">
                        <div class="d-sm-flex justify-content-center justify-content-sm-between">
                            <span class="text-muted text-center text-sm-left d-block d-sm-inline-block">Copyright © 2023 <a href="https://www.bootstrapdash.com/" target="_blank">BootstrapDash</a>. All rights reserved.</span>
                            <span class="float-none float-sm-right d-block mt-1 mt-sm-0 text-center">Hand-crafted & made with <i class="mdi mdi-heart text-danger"></i></span>
                        </div>
                    </footer>
                    <!-- partial -->
                </div>
                <!-- main-panel ends -->
            </div>
            <!-- page-body-wrapper ends -->
        </div>
        <!-- container-scroller -->
        <!-- plugins:js -->
        <script src="../assets/vendors/js/vendor.bundle.base.js"></script>
        <!-- endinject -->
        <!-- Plugin js for this page -->
        <script src="../assets/vendors/select2/select2.min.js"></script>
        <script src="../assets/vendors/typeahead.js/typeahead.bundle.min.js"></script>
        <!-- End plugin js for this page -->
        <!-- inject:js -->
        <script src="../assets/js/off-canvas.js"></script>
        <script src="../assets/js/misc.js"></script>
        <script src="../assets/js/settings.js"></script>
        <script src="../assets/js/todolist.js"></script>
        <script src="../assets/js/jquery.cookie.js"></script>
        <!-- endinject -->
        <!-- Custom js for this page -->
        <script src="../assets/js/file-upload.js"></script>
        <script src="../assets/js/typeahead.js"></script>
        <script src="../assets/js/select2.js"></script>

        <!-- End custom js for this page -->
    </body>
</html>


