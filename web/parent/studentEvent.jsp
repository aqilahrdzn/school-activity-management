<%-- 
    Document   : studentEvent
    Created on : May 25, 2025, 2:30:44 PM
    Author     : Lenovo
--%>
<%@page import="dao.NotificationDAO"%>
<%@page import="model.Notification"%>
<%@page import="model.Event"%>
<%@page import="model.Parent"%>
<%@page import="dao.ParentDAO"%>
<%-- 
    Document   : registerParent
    Created on : May 20, 2025, 11:10:56 PM
    Author     : Lenovo
--%>

<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="model.Student"%>
<%@page import="java.util.List"%>
<%@page import="util.DBConfig"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="model.Student" %>
<%@ page import="dao.ParentDAO" %>

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
        <%@ page import="dao.NotificationDAO" %>
        <%@ page import="model.Notification" %>
        <%@ page import="model.Parent" %>
        <%@ page import="java.util.List" %>

        <%
            Parent loggedInParent = (Parent) session.getAttribute("parent");
            List<Notification> notifications = null;
            int unreadCount = 0;

            if (loggedInParent != null) {
                NotificationDAO notificationDAO = new NotificationDAO();
                notifications = notificationDAO.getNotificationsByUserIdAndRole(loggedInParent.getId(), "parent");

                for (Notification note : notifications) {
                    if (note.getIsRead() == 0) {
                        unreadCount++;
                    }
                }
            }
        %>


        <%
            String email = (String) session.getAttribute("email"); // Retrieve email from session

            ParentDAO parentDAO = new ParentDAO();
            Parent parent = null;

            if (email != null) {
                parent = parentDAO.getParentByEmail(email); // Get parent details using email
            }

            if (parent == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return; // Stop further processing if parent not found
            }

            session.setAttribute("parent", parent); // Optional: store in session for later use
%>

        <div class="container-scroller">
            <!-- partial:../../partials/_navbar.html -->
            <nav class="navbar default-layout-navbar col-lg-12 col-12 p-0 fixed-top d-flex flex-row">
                <div class="text-center navbar-brand-wrapper d-flex align-items-center justify-content-start">
                    <a class="navbar-brand brand-logo" href="parentdashboard.jsp"><img src="../assets/images/skkj_logo.jpg" width="1000" height="50" alt="logo" /></a>
                    <a class="navbar-brand brand-logo-mini" href="../../index.html"><img src="../../assets/images/logo-mini.svg" alt="logo" /></a>
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
                                    <p class="mb-1 text-black"><%= parent.getName()%></p>
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
                                

                                <% if (unreadCount > 0) {%>
                                <span class="count-symbol bg-danger"><%= unreadCount%></span>
                                <% } %>
                            </a>
                            <div class="dropdown-menu dropdown-menu-end navbar-dropdown preview-list" aria-labelledby="notificationDropdown">
                                <h6 class="p-3 mb-0">Notifications</h6>
                                <div class="dropdown-divider"></div>

                                <% if (notifications != null && !notifications.isEmpty()) {
                for (Notification note : notifications) {%>
                                <a href="../MarkNotificationRead?id=<%= note.getId()%>" class="dropdown-item preview-item">
                                    <div class="preview-thumbnail">
                                        <div class="preview-icon <%= note.getIsRead() == 0 ? "bg-info" : "bg-secondary"%>">
                                            <i class="mdi mdi-information-outline"></i>
                                        </div>
                                    </div>
                                    <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                                        <h6 class="preview-subject font-weight-normal mb-1">
                                            <%= note.getIsRead() == 0 ? "New Notification" : "Notification"%>
                                        </h6>
                                        <p class="text-gray ellipsis mb-0"><%= note.getMessage()%></p>
                                    </div>
                                </a>
                                <div class="dropdown-divider"></div>
                                <% }
        } else { %>
                                <p class="text-center">No notifications</p>
                                <% }%>

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
                                    <img src="<%= (parent != null && parent.getProfilePicture() != null) ? "../profile_pics/" + parent.getProfilePicture() : "../assets/images/faces/default.jpg"%>" alt="profile" />
                                    <span class="login-status online"></span>
                                </div>

                                <div class="nav-profile-text d-flex flex-column">
                                    <span class="font-weight-bold mb-2"><%= parent != null ? parent.getName() : "Parent"%></span>
                                    <span class="text-secondary text-small">Parent</span>
                                </div>
                                <i class="mdi mdi-account-check text-info nav-profile-badge"></i>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="parentdashboard.jsp">
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
                                        <a class="nav-link" href="updateAccPr.jsp"><%= bundle.getString("update_account")%></a>
                                        <a class="nav-link" href="studentEvent.jsp"><%= bundle.getString("student_event_list")%></a>

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
                                        <a class="nav-link" href="../pages/charts/chartjs.html">ChartJs</a>
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
                                <div class="iframe-container">
                                            <iframe 
                                                src="https://calendar.google.com/calendar/embed?src=aqilah031103060404%40gmail.com&ctz=Asia%2FKuala_Lumpur" 
                                                style="border:0; width: 1000px; height: 500px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);" 
                                                frameborder="0" 
                                                scrolling="no">
                                            </iframe>
                                        </div>
                                <div class="card-body">
                                    <% if ("true".equals(request.getParameter("success"))) {%>
                                    <div class="alert alert-success" role="alert">
                                        <%= bundle.getString("approval_submitted")%>
                                    </div>
                                    <% } %>

                                    <h4 class="card-title">My Child's Events</h4>
                                    <div class="table-responsive">
                                        <%
                                            String parentEmail = (String) session.getAttribute("email");
                                            ParentDAO dao = new ParentDAO();

                                            // DAO returns Map<Student, List<Map<String, String>>> now
                                            Map<Student, List<Map<String, String>>> studentEvents = dao.getStudentEventsByParentEmail(parentEmail);
                                        %>

                                        <% if (studentEvents.isEmpty()) {%>
                                        <p><%= bundle.getString("no_events")%></p>
                                        <% } else {%>
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th><%= bundle.getString("name")%> </th>
                                                    <th><%= bundle.getString("classroom_name")%></th>
                                                    <th><%= bundle.getString("ic_number")%></th>
                                                    <th><%= bundle.getString("event_title")%></th>
                                                    <th><%= bundle.getString("approval_letter")%></th>
                                                    <th><%= bundle.getString("action")%></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    for (Map.Entry<Student, List<Map<String, String>>> entry : studentEvents.entrySet()) {
                                                        Student student = entry.getKey();
                                                        List<Map<String, String>> events = entry.getValue();
                                                        for (Map<String, String> event : events) {
                                                            String surat = event.get("surat_pengesahan");
                                                            String eventId = event.get("id"); // Get event ID here
%>
                                                <tr>
                                                    <td><%= student.getStudentName()%></td>
                                                    <td><%= student.getStudentClass()%></td>
                                                    <td><%= student.getIcNumber()%></td>
                                                    <td><%= event.get("title")%></td>
                                                    <td>
                                                        <% if (surat != null && !surat.trim().isEmpty()) {%>
                                                        <a href="<%= request.getContextPath() + "/ViewSuratServlet?eventId=" + eventId%>" target="_blank" class="btn btn-sm btn-success">
                                                            <%= bundle.getString("view_approval_letter")%>
                                                        </a>

                                                        <% } else {%>
                                                        <span><%= bundle.getString("no_letter")%></span>
                                                        <% }%>
                                                    </td>
                                                    <td>
                                                        <form method="get" action="approvalParent.jsp" style="display:inline;">
                                                            <input type="hidden" name="studentIc" value="<%= student.getIcNumber()%>" />
                                                            <input type="hidden" name="eventTitle" value="<%= event.get("title")%>" />
                                                            <button type="submit" class="btn btn-sm btn-primary"><%= bundle.getString("approval_button")%></button>
                                                        </form>

                                                    </td>
                                                </tr>
                                                <%      }
                                                    }
                                                %>
                                            </tbody>
                                        </table>
                                        <% }%>
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


