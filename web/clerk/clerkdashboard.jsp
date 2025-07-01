<%-- 
    Document   : teacherdashboard
    Created on : May 6, 2025, 8:36:45 PM
    Author     : Lenovo
--%>
<%@page import="model.Notification"%>
<%@page import="java.util.List"%>
<%@page import="model.Teacher"%>
<%@page import="dao.TeacherDAO"%>
<%@ page import="java.sql.*, util.DBConfig" %>
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
        <link rel="stylesheet" href="../assets/vendors/font-awesome/css/font-awesome.min.css" />
        <link rel="stylesheet" href="../assets/vendors/bootstrap-datepicker/bootstrap-datepicker.min.css">
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
            int totalParents = 0;

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
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM parent"); ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalParents = rs.getInt("total");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        %>
        <div class="container-scroller">
            <!-- partial:partials/_navbar.html -->
            <nav class="navbar default-layout-navbar col-lg-12 col-12 p-0 fixed-top d-flex flex-row">
                <div class="text-center navbar-brand-wrapper d-flex align-items-center justify-content-start">
                    <a class="navbar-brand brand-logo" href="index.jsp"><img src="../assets/images/logo.svg" alt="logo" /></a>
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
                        <li class="nav-item d-none d-lg-block full-screen-link">
                            <a class="nav-link">
                                <i class="mdi mdi-fullscreen" id="fullscreen-button"></i>
                            </a>
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
                                        <img src="assets/images/faces/face4.jpg" alt="image" class="profile-pic">
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject ellipsis mb-1 font-weight-normal">Mark send you a message</h6>
                                        <p class="text-gray mb-0"> 1 Minutes ago </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <img src="assets/images/faces/face2.jpg" alt="image" class="profile-pic">
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject ellipsis mb-1 font-weight-normal">Cregh send you a message</h6>
                                        <p class="text-gray mb-0"> 15 Minutes ago </p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <img src="assets/images/faces/face3.jpg" alt="image" class="profile-pic">
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
                        <% List<Notification> notifications = (List<Notification>) request.getAttribute("notifications"); %>

                        <li class="nav-item dropdown">
                            <a class="nav-link count-indicator dropdown-toggle" id="notificationDropdown" href="#" data-bs-toggle="dropdown">
                                <i class="mdi mdi-bell-outline"></i>
                                <span class="count-symbol bg-danger"></span>
                            </a>
                            <div class="dropdown-menu dropdown-menu-end navbar-dropdown preview-list" aria-labelledby="notificationDropdown">
                                <h6 class="p-3 mb-0">Notifications</h6>
                                <div class="dropdown-divider"></div>

                                <% if (notifications != null && !notifications.isEmpty()) {
                for (Notification note : notifications) {%>
                                <a class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <div class="preview-icon bg-info">
                                            <i class="mdi mdi-information-outline"></i>
                                        </div>
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject font-weight-normal mb-1">New Notification</h6>
                                        <p class="text-gray ellipsis mb-0"><%= note.getMessage()%></p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <%  }
        } else { %>
                                <p class="text-center">No notifications</p>
                                <% }%>

                                <h6 class="p-3 mb-0 text-center">See all notifications</h6>
                            </div>
                        </li>

                        <li class="nav-item nav-logout d-none d-lg-block">
                            <a class="nav-link" href="<%= request.getContextPath()%>/LoginServlet?action=logout">
                                <i class="mdi mdi-power"></i>
                            </a>
                        </li>
                        <li class="nav-item nav-settings d-none d-lg-block">
                            <a class="nav-link" href="#">
                                <i class="mdi mdi-format-line-spacing"></i>
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
                <!-- partial:partials/_sidebar.html -->
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
                        <!--            dashboard-->
                        <li class="nav-item">
                            <a class="nav-link" href="clerkdashboard.jsp">
                                <span class="menu-title"><%= bundle.getString("dashboard")%></span>
                                <i class="mdi mdi-home menu-icon"></i>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#forms">
                                <span class="menu-title"><%= bundle.getString("forms")%></span>
                                <i class="mdi mdi-format-list-bulleted menu-icon"></i>
                            </a>
                            <div class="collapse" id="forms">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="teacherRegistration.jsp"><%= bundle.getString("teacher_registration")%></a>
                                        <a class="nav-link" href="addVenue.jsp"><%= bundle.getString("add_new_venue")%></a>
                                        <a class="nav-link" href="updateVenue.jsp"><%= bundle.getString("update_venue_condition")%></a>
                                        <a class="nav-link" href="updateAccCk.jsp"><%= bundle.getString("update_account")%></a>
                                    </li>
                                </ul>
                            </div>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#charts">
                                <span class="menu-title"><%= bundle.getString("list")%></span>
                                <i class="mdi mdi-chart-bar menu-icon"></i>
                            </a>
                            <div class="collapse" id="charts">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="studentListCk.jsp"><%= bundle.getString("student_list")%></a>
                                        <a class="nav-link" href="teacherList.jsp"><%= bundle.getString("teacher_list")%></a>
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
                            <h3 class="page-title">
                                <span class="page-title-icon bg-gradient-primary text-white me-2">
                                    <i class="mdi mdi-home"></i>
                                </span> <%= bundle.getString("dashboard")%>
                            </h3>
                            <nav aria-label="breadcrumb">
                                <ul class="breadcrumb">
                                    <li class="breadcrumb-item active" aria-current="page">
                                        <%= bundle.getString("overview")%>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                        <!--                        total teacher-->
                        <div class="row">
                            <div class="col-md-4 stretch-card grid-margin">
                                <div class="card bg-gradient-danger card-img-holder text-white">
                                    <div class="card-body">
                                        <h4 class="font-weight-normal mb-3"><%= bundle.getString("total_teachers")%> <i class="mdi mdi-account-multiple mdi-24px float-end"></i></h4>
                                        <h2 class="mb-5"><%= totalTeachers%> <%= bundle.getString("teachers_label")%></h2>
                                        <h6 class="card-text"><%= bundle.getString("updated_realtime")%></h6>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 stretch-card grid-margin">
                                <div class="card bg-gradient-info card-img-holder text-white">
                                    <div class="card-body">
                                        <h4 class="font-weight-normal mb-3"><%= bundle.getString("total_students")%> <i class="mdi mdi-account-multiple mdi-24px float-end"></i></h4>
                                        <h2 class="mb-5"><%= totalStudents%> <%= bundle.getString("student")%></h2>
                                        <h6 class="card-text"><%= bundle.getString("updated_realtime")%></h6>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4 stretch-card grid-margin">
                                <div class="card bg-gradient-success card-img-holder text-white">
                                    <div class="card-body">
                                        <h4 class="font-weight-normal mb-3"><%= bundle.getString("total_registered_parents")%> <i class="mdi mdi-account-multiple mdi-24px float-end"></i></h4>
                                        <h2 class="mb-5"><%= totalParents%> <%= bundle.getString("parent")%></h2>
                                        <h6 class="card-text"><%= bundle.getString("updated_realtime")%></h6>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-lg-6 grid-margin stretch-card">
                                <div class="card">
                                    <div class="card-body">
                                        <h4 class="card-title"><%= bundle.getString("event_created_per_month")%></h4>
                                        <canvas id="barChart" style="height:300px"></canvas>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-5 grid-margin stretch-card">
                                <div class="card">
                                    <div class="card-body">
                                        <h4 class="card-title"><%= bundle.getString("traffic_sources")%></h4>
                                        <div class="doughnutjs-wrapper d-flex justify-content-center">
                                            <canvas id="traffic-chart"></canvas>
                                        </div>
                                        <div id="traffic-chart-legend" class="rounded-legend legend-vertical legend-bottom-left pt-4"></div>
                                    </div>
                                </div>
                            </div>


                        </div>

                        <!-- content-wrapper ends -->
                        <!-- partial:partials/_footer.html -->
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


            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
            <script src="<%= request.getContextPath()%>/assets/js/chart.js"></script>
            <!-- plugins:js -->
            <script src="../assets/vendors/js/vendor.bundle.base.js"></script>
            <!-- endinject -->
            <!-- Plugin js for this page -->
            <script src="../assets/vendors/chart.js/chart.umd.js"></script>
            <script src="../assets/vendors/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
            <!-- End plugin js for this page -->
            <!-- inject:js -->
            <script src="../assets/js/off-canvas.js"></script>
            <script src="../assets/js/misc.js"></script>
            <script src="../assets/js/settings.js"></script>
            <script src="../assets/js/todolist.js"></script>
            <script src="../assets/js/jquery.cookie.js"></script>
            <!-- endinject -->
            <!-- Custom js for this page -->
            <script src="../assets/js/dashboard.js"></script>
            <!-- End custom js for this page -->
    </body>
</html>

