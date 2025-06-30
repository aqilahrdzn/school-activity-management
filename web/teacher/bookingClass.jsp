<%-- 
    Document   : bookingClass
    Created on : May 6, 2025, 9:01:23 PM
    Author     : Lenovo
--%>

<%@page import="model.Teacher"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="util.DBConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="model.Classroom"%>
<%@page import="dao.ClassroomDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.List"%>
<%@page import="dao.EventDAO"%>
<%@page import="model.Event"%>
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
                <%@ page import="java.sql.*, java.util.*, dao.ClassroomDAO, dao.EventDAO, model.Event, model.Classroom" %>
                <%
                    Event preselectedEvent = null;

                    Integer eventId = (Integer) session.getAttribute("eventId");
                    if (eventId != null) {
                        EventDAO eventDAO = new EventDAO();
                        preselectedEvent = eventDAO.getEventById(eventId);
                    }

                    String eventTitle = (String) session.getAttribute("eventTitle");
                    String eventStart = (String) session.getAttribute("eventStartTime");
                    String eventEnd = (String) session.getAttribute("eventEndTime");

                    boolean buttonEnabled = (eventTitle != null && !eventTitle.isEmpty()
                            && eventStart != null && !eventStart.isEmpty()
                            && eventEnd != null && !eventEnd.isEmpty());

                    String teacherEmail = (String) session.getAttribute("email");
                    EventDAO eventDAO = new EventDAO();
                    List<Event> myEvents = eventDAO.getEventsByCreator(teacherEmail);

                    int calculatedDuration = 0;
                    if (eventStart != null && eventEnd != null) {
                        try {
                            java.time.LocalDateTime start = java.time.LocalDateTime.parse(eventStart);
                            java.time.LocalDateTime end = java.time.LocalDateTime.parse(eventEnd);
                            java.time.Duration durationBetween = java.time.Duration.between(start, end);
                            calculatedDuration = (int) durationBetween.toMinutes();
                        } catch (Exception e) {
                            out.println("<p style='color:red;'>Error parsing date/time: " + e.getMessage() + "</p>");
                        }
                    }

                    String startParam = request.getParameter("eventStart");
                    String durationParam = request.getParameter("duration");
                    String eventIdParam = request.getParameter("eventId");
//                    out.println(startParam);
                    Timestamp startTime = null;
                    int duration = 0;
                    String formattedStart = "";
                    List<Classroom> available = new ArrayList<>();

                    if (startParam != null && durationParam != null && eventIdParam != null) {
                        if (startParam != null && startParam.contains("T")) {
                            startTime = Timestamp.valueOf(startParam.replace("T", " ") + ":00");
                        } else {
                            out.println("<p style='color:red;'>Invalid start time format: " + startParam + "</p>");
                        }
                        try {
                            startTime = Timestamp.valueOf(startParam.replace("T", " ") + ":00");
                            duration = Integer.parseInt(durationParam);
                            Timestamp endTime = new Timestamp(startTime.getTime() + duration * 60 * 1000);
                            formattedStart = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime);

                            ClassroomDAO dao = new ClassroomDAO();
                            available = dao.getAvailableClassrooms(startTime, endTime);
                        } catch (Exception e) {
                            out.println("<p style='color:red;'>Error fetching available classrooms: " + e.getMessage() + "</p>");
                        }
                    }
                %>




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
                        <div class="row">
                            <div class="col-md-6 grid-margin stretch-card">
                                <div class="card">
                                    <div class="card-body">

                                        <h4 class="card-title"><%= bundle.getString("book_event_venue")%></h4>
                                        <form class="forms-sample" action="bookingClass.jsp" method="get">

                                            <div class="form-group">


                                                <% if (eventTitle != null && eventStart != null && eventEnd != null) {%>
                                                <div class="form-group">

                                                    <p><strong><%= bundle.getString("title_label")%></strong> <%= eventTitle%></p>
                                                    <p><strong><%= bundle.getString("start_time_label")%></strong> <%= eventStart%></p>
                                                    <p><strong><%= bundle.getString("end_time_label")%></strong> <%= eventEnd%></p>
                                                    <!-- Hidden inputs to send values in the request -->
                                                    <input type="hidden" name="eventTitle" value="<%= eventTitle%>" />
                                                    <input type="datetime-local" name="eventStart" value="<%= eventStart%>" readonly />
                                                    <input type="hidden" name="eventEndTime" value="<%= eventEnd%>" />
                                                    <input type="hidden" name="eventId" value="<%= (eventId != null) ? eventId : ""%>" />

                                                </div>
                                                <% } else {%>
                                                <p style="color:orange;"><%= bundle.getString("no_event_in_session")%></p>
                                                <% }%>
                                            </div>
                                            <div class="form-group">
                                                <label for="duration"><%= bundle.getString("duration_label")%>:</label>
                                                <input type="number" name="duration" id="duration" min="1"
                                                       value="<%= (durationParam != null) ? durationParam : calculatedDuration%>" 
                                                       readonly />
                                                <input type="hidden" value="<%= (durationParam != null) ? durationParam : calculatedDuration%>">
                                            </div>
                                            <br><br>
                                            <input type="submit" value="<%= bundle.getString("find_available_classroom")%>" <%= buttonEnabled ? "" : "disabled"%> />


                                        </form> 

                                        <% if (available != null && !available.isEmpty()) {%>
                                        <!-- Step 1: Classroom Selection -->
                                        <div class="form-group">
                                            <form id="selectForm">
                                                <label for="classroomSelect"><%= bundle.getString("select_classroom_label")%></label>
                                                <select name="classroomId" id="classroomSelect" required>
                                                    <% for (Classroom c : available) {%>
                                                    <option value="<%= c.getId()%>"><%= c.getName()%></option>
                                                    <% }%>
                                                </select>
                                                <br><br>
                                                <button type="button" onclick="showConfirmation()"><%= bundle.getString("continue_button")%></button>
                                            </form>
                                        </div>

                                        <div class="form-group">
                                            <form id="confirmationForm" method="post" action="<%= request.getContextPath()%>/classroom" style="display:none;">
                                                <input type="hidden" name="startTime" value="<%= formattedStart%>"/>
                                                <input type="hidden" name="duration" value="<%= duration%>"/>
                                                <input type="hidden" name="classroomId" id="hiddenClassroomId"/>
                                                <input type="hidden" name="eventId" value="<%= eventIdParam%>"/>

                                                <p><strong><%= bundle.getString("confirm_classroom")%>:</strong> <span id="confirmClassroomName"></span></p>
                                                <p><strong><%= bundle.getString("confirm_booking")%>:</strong> <%= formattedStart%></p>
                                                <p><strong><%= bundle.getString("duration_label")%>:</strong> <%= duration%> minutes</p>

                                                <input type="submit" value="Confirm Booking"/>
                                                <button type="button" onclick="cancelConfirmation()"><%= bundle.getString("cancel_button")%></button>
                                            </form>
                                        </div>

                                        <% } else if (startParam != null && durationParam != null && eventIdParam != null) {%>
                                        <p style="color:orange;"><%= bundle.getString("no_classroom_available")%>.</p>
                                        <% }%>



                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <script>
                        window.addEventListener("DOMContentLoaded", () => {
                            const input = document.getElementById("startTime");
                            if (input) {
                                const now = new Date();
                                now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
                                input.min = now.toISOString().slice(0, 16);
                            }
                        });

                        function showConfirmation() {
                            const select = document.getElementById("classroomSelect");
                            if (!select || select.selectedIndex === -1)
                                return;

                            const selected = select.options[select.selectedIndex];
                            if (!selected)
                                return;

                            // Update confirmation form fields
                            document.getElementById("confirmClassroomName").textContent = selected.text;
                            document.getElementById("hiddenClassroomId").value = selected.value;

                            // Toggle form visibility
                            document.getElementById("selectForm").style.display = "none";
                            document.getElementById("confirmationForm").style.display = "block";
                        }

                        function cancelConfirmation() {
                            // Hide confirmation form and show classroom selection again
                            document.getElementById("confirmationForm").reset(); // optional: resets hidden fields
                            document.getElementById("confirmationForm").style.display = "none";
                            document.getElementById("selectForm").style.display = "block";
                        }
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
