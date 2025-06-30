<%-- 
    Document   : eventDetails
    Created on : May 18, 2025, 9:16:10 PM
    Author     : Lenovo
--%>


<%@page import="model.Teacher"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="util.DBConfig"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String lang = request.getParameter("lang");
    if (lang != null) {
        session.setAttribute("lang", lang);
    }
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) {
        currentLang = "ms"; // Default: BM
    }
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages", new java.util.Locale(currentLang));
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Purple Admin</title>
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
            String email = (String) session.getAttribute("email"); // Retrieve email from session

            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = null;

            if (email != null) {
                teacher = teacherDAO.getTeacherDetails(email); // Pass email to fetch details
            }

            // Add a check to handle cases where teacher is null (e.g., not logged in)
            if (teacher == null) {
                // Redirect to login page or display an error message
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return; // Stop further processing of this JSP
            }
        %>
        <%
            int totalTeachers = 0;
            int totalStudents = 0;

            try (Connection conn = DBConfig.getConnection()) {
                // Get total teachers
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM teachers"); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalTeachers = rs.getInt("total");
                    }
                }

                // Get total students
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM student"); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalStudents = rs.getInt("total");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
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
                            <a class="nav-link" href="#">
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
                    <%@ page import="java.sql.*" %>
                    <%@ page import="util.DBConfig" %>

                    <%
                        String eventId = request.getParameter("eventId");
                        String eventTitle = "";
                        String eventStartTime = "";
                        String eventEndTime = "";
                        String uploadedDescription = "";
                        boolean hasUploadedFiles = false;

                        if (eventId != null && !eventId.isEmpty()) {
                            String query = "SELECT e.title, e.start_time, e.end_time, eu.description "
                                    + "FROM events e LEFT JOIN event_uploads eu ON e.id = eu.event_id WHERE e.id = ?";
                            try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                                stmt.setInt(1, Integer.parseInt(eventId));
                                try (ResultSet rs = stmt.executeQuery()) {
                                    if (rs.next()) {
                                        eventTitle = rs.getString("title");
                                        eventStartTime = rs.getString("start_time");
                                        eventEndTime = rs.getString("end_time");
                                        uploadedDescription = rs.getString("description") != null ? rs.getString("description") : "";
                                    }
                                }

                                // Check if any upload exists
                                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM event_uploads WHERE event_id = ?")) {
                                    checkStmt.setInt(1, Integer.parseInt(eventId));
                                    try (ResultSet rs = checkStmt.executeQuery()) {
                                        if (rs.next() && rs.getInt(1) > 0) {
                                            hasUploadedFiles = true;
                                        }
                                    }
                                }

                            } catch (SQLException e) {
                                out.println("Error: " + e.getMessage());
                            }
                        }

                        String message = (String) request.getAttribute("message");
                        if (message == null && request.getSession().getAttribute("message") != null) {
                            message = (String) request.getSession().getAttribute("message");
                            request.getSession().removeAttribute("message");
                        }
                    %>

                    <div class="col-12 grid-margin stretch-card">
                        <div class="card">
                            <div class="card-body">
                                <h4 class="card-title"><%= bundle.getString("event_details_title")%></h4>
                                <p class="card-description"><%= bundle.getString("event_description_upload")%></p>

                                <% if (message != null) {%>
                                <div class="alert alert-info"><%= message%></div>
                                <% } %>

                                <% if (!hasUploadedFiles) {%>
                                <form class="forms-sample" method="post" action="<%= request.getContextPath()%>/UploadEventFileServlet" enctype="multipart/form-data">
                                    <input type="hidden" name="eventId" value="<%= eventId != null ? eventId : ""%>">

                                    <div class="form-group">
                                        <label><%= bundle.getString("event_title_label")%></label>
                                        <input type="text" class="form-control" value="<%= eventTitle%>" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label><%= bundle.getString("event_datetime_label")%></label>
                                        <input type="text" class="form-control" value="<%= eventStartTime%> - <%= eventEndTime%>" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label><%= bundle.getString("upload_files_label")%> 1</label>
                                        <input type="file" name="eventFile1" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label><%= bundle.getString("upload_files_label")%> 2</label>
                                        <input type="file" name="eventFile2" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label><%= bundle.getString("upload_files_label")%> 3</label>
                                        <input type="file" name="eventFile3" class="form-control" required>
                                    </div>

                                    <div class="form-group">
                                        <label><%= bundle.getString("description_label")%></label>
                                        <textarea name="description" class="form-control" rows="4" required><%= uploadedDescription%></textarea>
                                    </div>

                                    <button type="submit" class="btn btn-gradient-primary"><%= bundle.getString("submit_button")%></button>
                                </form>
                                <% } else {%>

                                <div class="form-group">
                                    <label><%= bundle.getString("event_title_label")%></label>
                                    <input type="text" class="form-control" value="<%= eventTitle%>" readonly>
                                </div>

                                <div class="form-group">
                                    <label><%= bundle.getString("event_datetime_label")%></label>
                                    <input type="text" class="form-control" value="<%= eventStartTime%> - <%= eventEndTime%>" readonly>
                                </div>

                                <div class="form-group">
                                    <label><%= bundle.getString("description_label")%></label>
                                    <textarea class="form-control" rows="4" readonly><%= uploadedDescription%></textarea>
                                </div>

                                <a href="<%= request.getContextPath()%>/GenerateOPRServlet?eventId=<%= eventId%>" 
                                   target="_blank" class="btn btn-gradient-info"><%= bundle.getString("view_opr_button")%></a>

                                <a href="editEventDetails.jsp?eventId=<%= eventId%>" 
                                   class="btn btn-warning ml-2"><%= bundle.getString("edit_event_details_button")%></a>

                                <% }%>
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

