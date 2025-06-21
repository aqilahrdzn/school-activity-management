<%-- 
    Document   : studentList
    Created on : Jun 19, 2025, 10:49:01 AM
    Author     : Lenovo
--%>

<%@page import="model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="model.Teacher"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="util.DBConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>

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
            // Retrieve email from session
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

            // Retrieve events
            List<Map<String, String>> events = new ArrayList<>();
            String eventQuery = "SELECT id, title, description, start_time, end_time, target_class, status FROM events WHERE created_by = ?";

            try (Connection connection = DBConfig.getConnection(); PreparedStatement eventStmt = connection.prepareStatement(eventQuery)) {

                eventStmt.setString(1, email);
                try (ResultSet rs = eventStmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> event = new HashMap<>();
                        event.put("id", String.valueOf(rs.getInt("id")));
                        event.put("title", rs.getString("title"));
                        event.put("description", rs.getString("description"));
                        event.put("start_time", rs.getString("start_time"));
                        event.put("end_time", rs.getString("end_time"));
                        event.put("target_class", rs.getString("target_class"));
                        event.put("status", rs.getString("status"));
                        events.add(event);
                    }
                }
            } catch (SQLException e) {
                request.setAttribute("eventError", "Error retrieving event data: " + e.getMessage());
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
                                <span class="menu-title">Dashboard</span>
                                <i class="mdi mdi-home menu-icon"></i>
                            </a>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#forms" aria-expanded="false" aria-controls="forms">
                                <span class="menu-title">Forms</span>
                                <i class="mdi mdi-format-list-bulleted menu-icon"></i>
                            </a>
                            <div class="collapse" id="forms">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="studentRegistration.jsp">Student Registration</a>
                                        <a class="nav-link" href="createEvent.jsp">Create Event/Activity</a>
                                        <a class="nav-link" href="bookingClass.jsp">Booking Event Venue</a>
                                        <a class="nav-link" href="eventList.jsp">Event List</a>
                                        <a class="nav-link" href="updateAccTc.jsp">Update Account</a>
                                    </li>
                                </ul>
                            </div>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" data-bs-toggle="collapse" href="#charts" aria-expanded="false" aria-controls="charts">
                                <span class="menu-title">Charts</span>
                                <i class="mdi mdi-chart-bar menu-icon"></i>
                            </a>
                            <div class="collapse" id="charts">
                                <ul class="nav flex-column sub-menu">
                                    <li class="nav-item">
                                        <a class="nav-link" href="../../pages/charts/chartjs.html">ChartJs</a>
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

                    <%@page import="model.Teacher"%>
                    <%@page import="java.util.List, model.Student, model.Teacher"%>
                    <%
                        // ... existing Teacher session retrieval and checks ...

                        String successMessage = request.getParameter("successMessage");
                        String errorMessage = request.getParameter("errorMessage");
                    %>

                    <!-- Add these div blocks near the top of your card-body in studentList.jsp -->
                    <% if (successMessage != null) {%>
                    <div class="alert alert-success mt-3" role="alert">
                        <%= successMessage%>
                    </div>
                    <% } %>
                    <% if (errorMessage != null) {%>
                    <div class="alert alert-danger mt-3" role="alert">
                        <%= errorMessage%>
                    </div>
                    <% } %>

                    <!-- ... rest of your studentList.jsp ... -->
                    <%
                        Teacher loggedInTeacher = (Teacher) session.getAttribute("teacher");

                        if (loggedInTeacher == null) {
                            response.sendRedirect(request.getContextPath() + "/login.jsp");
                            return;
                        }

                        boolean isCurrentUserGuruKelas = "Yes".equals(loggedInTeacher.getIsGuruKelas());
                        String currentUserKelas = loggedInTeacher.getKelas();
                    %>

                    <div class="row">
                        <div class="col-12 grid-margin">
                            <div class="card">
                                <div class="card-body">
                                    <h4 class="card-title">Student List</h4>

                                    <form id="studentClassForm"> <%-- Add an ID to the form --%>
                                        <div class="form-group row">
                                            <label for="studentClass" class="col-sm-2 col-form-label">Select Class:</label>
                                            <div class="col-sm-6">
                                                <select class="form-control" name="studentClass" id="studentClass" required>
                                                    <option value="">-- Select Class --</option>
                                                    <option value="1 Makkah">1 Makkah</option>
                                                    <option value="1 Madinah">1 Madinah</option>
                                                    <option value="2 Makkah">2 Makkah</option>
                                                    <option value="2 Madinah">2 Madinah</option>
                                                    <option value="3 Makkah">3 Makkah</option>
                                                    <option value="3 Madinah">3 Madinah</option>
                                                    <option value="4 Makkah">4 Makkah</option>
                                                    <option value="4 Madinah">4 Madinah</option>
                                                    <option value="5 Makkah">5 Makkah</option>
                                                    <option value="5 Madinah">5 Madinah</option>
                                                    <option value="6 Makkah">6 Makkah</option>
                                                    <option value="6 Madinah">6 Madinah</option>
                                                </select>
                                            </div>
                                            <div class="col-sm-2">
                                                <button type="submit" class="btn btn-primary">View</button>
                                            </div>
                                        </div>
                                    </form>

                                    <div class="table-responsive">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th> Name </th>
                                                    <th> IC Number </th>
                                                    <th> Sport Team </th>
                                                    <th> Uniform Unit </th>
                                                        
                                                </tr>
                                            </thead>
                                            <tbody id="studentTableBody"> <%-- Add an ID to the tbody --%>
                                                <tr><td colspan="4">Please select a class to view students.</td></tr>

                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
                    <script>
                        $(document).ready(function () {
                            // Pre-select the class if the user is Guru Kelas and has an assigned class
                            var isGuruKelas = "<%= isCurrentUserGuruKelas%>"; // Get boolean from JSP
                            var currentUserKelas = "<%= currentUserKelas%>"; // Get string from JSP

                            if (isGuruKelas === "true" && currentUserKelas && currentUserKelas !== "null" && currentUserKelas !== "") {
                                $('#studentClass').val(currentUserKelas).change(); // Set value and trigger change to load data
                            }

                            $('#studentClassForm').submit(function (event) {
                                event.preventDefault(); // Prevent default form submission

                                var selectedClass = $('#studentClass').val();
                                var tableBody = $('#studentTableBody');
                                tableBody.empty(); // Clear existing rows

                                if (selectedClass) {
                                    $.ajax({
                                        url: '<%= request.getContextPath()%>/StudentListJsonController', // Call the new JSON servlet
                                        type: 'GET',
                                        data: {studentClass: selectedClass},
                                        dataType: 'json', // Expect JSON response
                                        success: function (data) {
                                            if (data && data.length > 0) {
                                                $.each(data, function (index, student) {
                                                    var row = '<tr>' +
                                                            '<td>' + student.studentName + '</td>' +
                                                            '<td>' + student.icNumber + '</td>' +
                                                            '<td>' + student.sportTeam + '</td>' +
                                                            '<td>' + student.uniformUnit + '</td>';

                                                    
                                                    row += '</tr>';
                                                    tableBody.append(row);
                                                });
                                            } else {
                                                tableBody.append('<tr><td colspan="4">No students found for selected class.</td></tr>');

                                            }
                                        },
                                        error: function (jqXHR, textStatus, errorThrown) {
                                            console.log("AJAX error: " + textStatus + ', ' + errorThrown);
                                            tableBody.append('<tr><td colspan="4">Error loading students.</td></tr>');

                                        }
                                    });
                                } else {
                                    tableBody.append('<tr><td colspan="<%= isCurrentUserGuruKelas ? "5" : "4"%>">Please select a class to view students.</td></tr>');
                                }
                            });
                            // Trigger submission on initial page load if class is pre-selected by Guru Kelas
                            if (isGuruKelas === "true" && currentUserKelas && currentUserKelas !== "null" && currentUserKelas !== "") {
                                $('#studentClassForm').submit();
                            }
                        });
                    </script>
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


