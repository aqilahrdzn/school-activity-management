<%-- 
    Document   : editApproval
    Created on : Jun 27, 2025, 12:56:56 AM
    Author     : Lenovo
--%>
<%@page import="dao.NotificationDAO"%>
<%@page import="model.Notification"%>

<%@page import="model.Parent"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>

<%@page import="model.Student"%>
<%@page import="java.util.List"%>


<%@page import="util.DBConfig"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String lang = request.getParameter("lang");
    if (lang != null) session.setAttribute("lang", lang);
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) currentLang = "ms";
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
        <%@ page import="java.sql.*, model.Parent, util.DBConfig" %>
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
            Parent parent = (Parent) session.getAttribute("parent");
            if (parent == null) {
                response.sendRedirect("../login.jsp");
                return;
            }

            boolean success = "true".equals(request.getParameter("success"));

            String eventTitle = request.getParameter("eventTitle");
            String studentIc = request.getParameter("studentIc");

            if (eventTitle == null || studentIc == null) {
                out.println("<p style='color:red;'>Error: Missing event title or student IC.</p>");
                return;
            }

            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            int eventId = -1;
            int parentId = -1;
            String eventCategory = null;
            String status = "", reason = "", resitPath = "";
            boolean alreadySubmitted = false;
            boolean hasResit = false;

            try {
                con = DBConfig.getConnection();

                // Get parent_id from student
                ps = con.prepareStatement("SELECT parent_id FROM student WHERE ic_number = ?");
                ps.setString(1, studentIc);
                rs = ps.executeQuery();
                if (rs.next()) {
                    parentId = rs.getInt("parent_id");
                } else {
                    out.println("<p style='color:red;'>Error: No parent found for this student.</p>");
                    return;
                }
                rs.close();
                ps.close();

                // Get event_id and category
                ps = con.prepareStatement("SELECT id, category FROM events WHERE title = ?");
                ps.setString(1, eventTitle);
                rs = ps.executeQuery();
                if (rs.next()) {
                    eventId = rs.getInt("id");
                    eventCategory = rs.getString("category");
                } else {
                    out.println("<p style='color:red;'>Error: Event not found.</p>");
                    return;
                }
                rs.close();
                ps.close();

                // Check if already submitted and fetch existing data
                ps = con.prepareStatement(
                        "SELECT status, reason, resit_file FROM parent_approval WHERE event_id = ? AND parent_id = ?"
                );
                ps.setInt(1, eventId);
                ps.setInt(2, parentId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    alreadySubmitted = true;
                    status = rs.getString("status");
                    reason = rs.getString("reason");
                    resitPath = rs.getString("resit_file");
                    hasResit = resitPath != null && !resitPath.trim().isEmpty();
                }
            } catch (Exception e) {
                out.println("<p style='color:red;'>Database error: " + e.getMessage() + "</p>");
                return;
            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (Exception e) {
                }
                if (ps != null) try {
                    ps.close();
                } catch (Exception e) {
                }
                if (con != null) try {
                    con.close();
                } catch (Exception e) {
                }
            }
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
  

<<li class="nav-item dropdown">
    <a class="nav-link count-indicator dropdown-toggle" id="notificationDropdown" href="#" data-bs-toggle="dropdown">
        <i class="mdi mdi-bell-outline"></i>
        <% if (unreadCount > 0) { %>
            <span class="count-symbol bg-danger"><%= unreadCount %></span>
        <% } %>
    </a>
    <div class="dropdown-menu dropdown-menu-end navbar-dropdown preview-list" aria-labelledby="notificationDropdown">
        <h6 class="p-3 mb-0">Notifications</h6>
        <div class="dropdown-divider"></div>

        <% if (notifications != null && !notifications.isEmpty()) {
            for (Notification note : notifications) { %>
            <a href="../MarkNotificationRead?id=<%= note.getId() %>" class="dropdown-item preview-item">
                <div class="preview-thumbnail">
                    <div class="preview-icon <%= note.getIsRead() == 0 ? "bg-info" : "bg-secondary" %>">
                        <i class="mdi mdi-information-outline"></i>
                    </div>
                </div>
                <div class="preview-item-content d-flex align-items-start flex-column justify-content-center">
                    <h6 class="preview-subject font-weight-normal mb-1">
                        <%= note.getIsRead() == 0 ? "New Notification" : "Notification" %>
                    </h6>
                    <p class="text-gray ellipsis mb-0"><%= note.getMessage() %></p>
                </div>
            </a>
            <div class="dropdown-divider"></div>
        <% }} else { %>
            <p class="text-center">No notifications</p>
        <% } %>

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
                                        <a class="nav-link" href="updateAccPr.jsp"><%= bundle.getString("update_account")%></a>
                                        <a class="nav-link" href="studentEvent.jsp"><%= bundle.getString("student_event_list")%></a>

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
                    <div class="col-md-6 grid-margin stretch-card">
                        <div class="card">
                            <div class="card-body">
                                <h4 class="card-title"><%= bundle.getString("edit_approval")%></h4>
                                <form method="post" action="<%= request.getContextPath() %>/SubmitApprovalServlet" enctype="multipart/form-data">
                                    <input type="hidden" name="event_id" value="<%= eventId%>">
                                    <input type="hidden" name="parent_id" value="<%= parentId%>">
                                    <input type="hidden" name="event_category" value="<%= eventCategory%>">

                                    <div class="form-group">
                                        <label><%= bundle.getString("update_status")%></label><br>
                                        <input type="radio" name="status" value="Approved" <%= "Approved".equalsIgnoreCase(status) ? "checked" : ""%>> <%= bundle.getString("approve")%>
                                        <input type="radio" name="status" value="Rejected" <%= "Rejected".equalsIgnoreCase(status) ? "checked" : ""%>> <%= bundle.getString("reject")%>
                                    </div>

                                    <div class="form-group">
                                        <label for="reason"><%= bundle.getString("reason_if_reject")%>:</label>
                                        <input type="text" class="form-control" name="reason" value="<%= reason%>">
                                    </div>

                                    <% if ("payment".equalsIgnoreCase(eventCategory)) { %>
                                    <div class="form-group">
                                        <label for="resit"><%= bundle.getString("upload_new_resit")%>:</label>
                                        <input type="file" class="form-control" name="resit" accept=".pdf,.jpg,.jpeg,.png">
                                        <% if (hasResit) {%>
                                        <p><%= bundle.getString("current_resit")%>: <a href="../ViewResitServlet?event_id=<%= eventId%>&parent_id=<%= parentId%>" target="_blank"><%= bundle.getString("view")%></a></p>
                                        <% } %>
                                    </div>
                                    <% }%>

                                    <button type="submit" class="btn btn-primary"><%= bundle.getString("update_approval")%></button>
                                </form>
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



