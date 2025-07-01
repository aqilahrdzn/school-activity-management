<%-- 
    Document   : reportList
    Created on : May 19, 2025, 10:05:31 PM
    Author     : Lenovo
--%>

<%@page import="model.Parent"%>
<%@page import="model.Student"%>
<%@page import="dao.StudentDAO"%>
<%@page import="dao.ParentDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.time.format.TextStyle"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.time.YearMonth"%>
<%@page import="model.Teacher"%>
<%@page import="java.sql.Statement"%>
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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
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
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            if (teacher == null) {
                response.sendRedirect("../login.jsp");
                return;
            }

            boolean success = "true".equals(request.getParameter("success"));
        %>
        <div class="container-scroller">
            <!-- partial:../../partials/_navbar.html -->
            <nav class="navbar default-layout-navbar col-lg-12 col-12 p-0 fixed-top d-flex flex-row">
                <div class="text-center navbar-brand-wrapper d-flex align-items-center justify-content-start">
                    <a class="navbar-brand brand-logo" href="../../index.html"><img src="../../assets/images/logo.svg" alt="logo" /></a>
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
                                <div class="nav-profile-img">
                                    <img src="../../assets/images/faces/face1.jpg" alt="image">
                                    <span class="availability-status online"></span>
                                </div>
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
                        <!--            dashboard-->
                        <li class="nav-item">
                            <a class="nav-link" href="hmdashboard.jsp">
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
                                        <a class="nav-link" href="reportList.jsp"><%= bundle.getString("report_list")%></a>
                                        <a class="nav-link" href="updateAccHm.jsp"><%= bundle.getString("update_account")%></a>

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
                    <div class="container mt-4">
                        <h3 class="mb-4 text-center"><%= bundle.getString("headmaster_report_center")%></h3>

                        <ul class="nav nav-tabs mb-3">
                            <li class="nav-item">
                                <a class="nav-link" id="parentTab-tab" href="#" onclick="showTab('parentTab')"><%= bundle.getString("parent_report")%></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="studentTab-tab" href="#" onclick="showTab('studentTab')"><%= bundle.getString("student_report")%></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="teacherTab-tab" href="#" onclick="showTab('teacherTab')"><%= bundle.getString("teacher_report")%></a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="eventTab-tab" href="#" onclick="showTab('eventTab')"><%= bundle.getString("event_report")%></a>
                            </li>

                        </ul>

                        <div class="report-tab" id="parentTab" style="display:none;">
                            <h4><%= bundle.getString("parent_list_by_class")%></h4>

                            <form method="get" action="reportList.jsp" class="form-inline mb-3">
                                <input type="hidden" name="tab" value="parentTab" />
                                <label class="mr-2"><%= bundle.getString("select_class")%>:</label>
                                <select name="classFilter" class="form-control mr-2" onchange="this.form.submit()">
                                    <option value=""><%= bundle.getString("all_classes")%></option>
                                    <%
                                        String[] classes = {"1 Makkah", "1 Madinah", "2 Makkah", "2 Madinah", "3 Makkah"};
                                        String classFilter = request.getParameter("classFilter");
                                        for (String c : classes) {
                                            String selected = c.equals(classFilter) ? "selected" : "";
                                    %>
                                    <option value="<%= c%>" <%= selected%>><%= c%></option>
                                    <% }%>
                                </select>
                            </form>
                            <div class="table-responsive"> 
                                <table class="table table-bordered table-hover">
                                    <thead class="thead-light">
                                        <tr>
                                            <th>#</th>
                                            <th><%= bundle.getString("parent_name")%></th>
                                            <th><%= bundle.getString("email")%></th>
                                            <th><%= bundle.getString("contact_number")%></th>
                                            <th><%= bundle.getString("ic_number")%></th>
                                            <th><%= bundle.getString("children")%></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            ParentDAO parentDAO = new ParentDAO();
                                            StudentDAO studentDAO = new StudentDAO();
                                            List<Student> allStudents = new ArrayList<>();
                                            if (classFilter != null && !classFilter.isEmpty()) {
                                                allStudents = studentDAO.getStudentsByClass(classFilter);
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
                                        <% }
                                            if (parentMap.isEmpty()) {%>
                                        <tr><td colspan="6" class="text-center"><%= bundle.getString("no_parent")%>.</td></tr>
                                        <% }%>
                                    </tbody>
                                </table>
                            </div>
                            <form method="get" action="parentReport.jsp" target="_blank" class="mb-3">
                                <input type="hidden" name="classFilter" value="<%= classFilter != null ? classFilter : ""%>">
                                
                            </form>
                        </div>

                        <div class="report-tab" id="studentTab" style="display:none;">
                            <div class="container">
                                <h4><%= bundle.getString("student_list_by_class")%>/h4>

                                    <form method="get" action="studentReport.jsp" target="_blank" class="mb-3">
                                        <input type="hidden" name="selectedClass" value="<%= request.getParameter("selectedClass") != null ? request.getParameter("selectedClass") : ""%>">
                                        
                                    </form>

                                    <form method="get" action="reportList.jsp" class="form-inline mb-3">
                                        <input type="hidden" name="tab" value="studentTab" />
                                        <label class="mr-2"><%= bundle.getString("select_class")%></label>
                                        <select name="selectedClass" class="form-control mr-2" onchange="this.form.submit()">
                                            <option value=""><%= bundle.getString("all_classes")%></option>
                                            <%
                                                String[] studentClasses = {"1 Makkah", "1 Madinah", "2 Makkah", "2 Madinah", "3 Makkah"};
                                                String selectedClass = request.getParameter("selectedClass");
                                                for (String c : studentClasses) {
                                                    String selected = c.equals(selectedClass) ? "selected" : "";
                                            %>
                                            <option value="<%= c%>" <%= selected%>><%= c%></option>
                                            <% }%>
                                        </select>
                                    </form>
                                    <div class="table-responsive"> 
                                        <table class="table table-bordered">
                                            <thead class="thead-light">
                                                <tr>
                                                    <th>#</th>
                                                    <th><%= bundle.getString("name")%></th>
                                                    <th><%= bundle.getString("ic_number")%></th>
                                                    <th><%= bundle.getString("sport_team")%></th>
                                                    <th><%= bundle.getString("uniform_unit")%></th>

                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    dao.StudentDAO studentDAO_std = new dao.StudentDAO();
                                                    dao.ParentDAO parentDAO_std = new dao.ParentDAO();
                                                    List<model.Student> students = selectedClass != null && !selectedClass.isEmpty()
                                                            ? studentDAO_std.getStudentsByClass(selectedClass)
                                                            : new ArrayList<model.Student>();

                                                    int studentCount = 1;
                                                    for (model.Student s : students) {
                                                        model.Parent parent = parentDAO_std.getParentByStudentIc(s.getIcNumber());
                                                %>
                                                <tr>
                                                    <td><%= studentCount++%></td>
                                                    <td><%= s.getStudentName()%></td>
                                                    <td><%= s.getIcNumber()%></td>
                                                    <td><%= s.getSportTeam()%></td>
                                                    <td><%= s.getUniformUnit()%></td>

                                                </tr>
                                                <% }
                                                    if (students.isEmpty()) {%>
                                                <tr><td colspan="6" class="text-center"><%= bundle.getString("no_parent")%></td></tr>
                                                    <% }%>
                                            </tbody>
                                        </table>
                                    </div>
                            </div>
                        </div>


                        <div class="report-tab" id="teacherTab" style="display:none;">
                            <div class="container">
                                <h4><%= bundle.getString("teacher_list_by_class")%></h4>

                                <form method="get" action="teacherReport.jsp" target="_blank" class="mb-3">
                                    <input type="hidden" name="classFilter" value="<%= request.getParameter("classFilter") != null ? request.getParameter("classFilter") : ""%>">
                                    
                                </form>

                                <form method="get" action="reportList.jsp" class="form-inline mb-3">
                                    <input type="hidden" name="tab" value="teacherTab" />
                                    <label class="mr-2"><%= bundle.getString("filter_by_class")%></label>
                                    <select name="classFilter" class="form-control mr-2" onchange="this.form.submit()">
                                        <option value=""><%= bundle.getString("all_classes")%></option>
                                        <%
                                            dao.TeacherDAO teacherDAO = new dao.TeacherDAO();
                                            List<String> assignedClasses = teacherDAO.getAssignedClasses();
                                            String selectedTeacherClass = request.getParameter("classFilter");

                                            for (String cls : assignedClasses) {
                                                String selected = cls.equals(selectedTeacherClass) ? "selected" : "";
                                        %>
                                        <option value="<%= cls%>" <%= selected%>><%= cls%></option>
                                        <% }%>
                                    </select>
                                </form>
                                <div class="table-responsive"> 
                                    <table class="table table-bordered table-hover">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>#</th>
                                                <th><%= bundle.getString("name")%></th>
                                                <th><%= bundle.getString("email")%></th>
                                                <th><%= bundle.getString("contact_number")%></th>
                                                <th><%= bundle.getString("role")%></th>
                                                <th><%= bundle.getString("class_assigned")%></th>
                                                <th><%= bundle.getString("guru_kelas")%></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<model.Teacher> teachers;

                                                if (selectedTeacherClass != null && !selectedTeacherClass.isEmpty()) {
                                                    teachers = new ArrayList<model.Teacher>();
                                                    model.Teacher filteredTeacher = teacherDAO.getTeacherByClass(selectedTeacherClass);
                                                    if (filteredTeacher != null) {
                                                        teachers.add(filteredTeacher);
                                                    }
                                                } else {
                                                    teachers = teacherDAO.getTeachersByRole("Teacher");
                                                }

                                                int i = 1;
                                                for (model.Teacher t : teachers) {
                                            %>
                                            <tr>
                                                <td><%= i++%></td>
                                                <td><%= t.getName()%></td>
                                                <td><%= t.getEmail()%></td>
                                                <td><%= t.getContactNumber()%></td>
                                                <td><%= t.getRole()%></td>
                                                <td><%= t.getKelas() != null ? t.getKelas() : "-"%></td>
                                                <td><%= "Yes".equalsIgnoreCase(t.getIsGuruKelas()) ? "Yes" : "No"%></td>
                                            </tr>
                                            <% }
                                                if (teachers.isEmpty()) {%>
                                            <tr><td colspan="7" class="text-center"><%= bundle.getString("no_teacher")%>.</td></tr>
                                            <% }%>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="report-tab" id="eventTab" style="display:none;">
                            <div class="container">
                                <h4><%= bundle.getString("event_report")%></h4>

                                <form method="get" action="eventReport.jsp" target="_blank" class="mb-3">
                                    <input type="hidden" name="category" value="<%= request.getParameter("category") != null ? request.getParameter("category") : ""%>">
                                    <input type="hidden" name="filterMonthYear" value="<%= request.getParameter("filterMonthYear") != null ? request.getParameter("filterMonthYear") : ""%>">
                                    
                                </form>

                                <form class="form-inline mb-3" method="get" action="reportList.jsp">
                                    <input type="hidden" name="tab" value="eventTab" />
                                    <label class="mr-2"><%= bundle.getString("filter_by_category")%>:</label>
                                    <select name="category" class="form-control mr-3" onchange="this.form.submit()">
                                        <option value=""><%= bundle.getString("all")%></option>
                                        <%
                                            String[] categories = {"School", "External", "Payment"};
                                            String selectedCat = request.getParameter("category");
                                            for (String cat : categories) {
                                                String selected = cat.equalsIgnoreCase(selectedCat) ? "selected" : "";
                                        %>
                                        <option value="<%= cat%>" <%= selected%>><%= cat%></option>
                                        <% }%>
                                    </select>

                                    <label class="mr-2"><%= bundle.getString("filter_by_month")%>:</label>
                                    <input type="month" class="form-control" name="filterMonthYear"
                                           value="<%= request.getParameter("filterMonthYear") != null ? request.getParameter("filterMonthYear") : ""%>"
                                           onchange="this.form.submit()">
                                </form>
                                <div class="table-responsive"> 
                                    <table class="table table-bordered table-striped">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>#</th>
                                                <th><%= bundle.getString("event_title")%></th>
                                                <th><%= bundle.getString("category")%></th>
                                                <th><%= bundle.getString("start_date")%></th>
                                                <th><%= bundle.getString("end_date")%></th>
                                                <th><%= bundle.getString("created_by")%></th>
                                                <th><%= bundle.getString("payment")%> (RM)</th>
                                                <th><%= bundle.getString("participant")%></th>
                                                <th><%= bundle.getString("view_opr")%></th>

                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                dao.EventDAO eventDAO = new dao.EventDAO();
                                                dao.EventParticipantDAO participantDAO = new dao.EventParticipantDAO();

                                                List<model.Event> events = eventDAO.getAllEvents();
                                                String categoryFilter = request.getParameter("category");
                                                String monthYearFilter = request.getParameter("filterMonthYear");
                                                int eventCount = 1;

                                                for (model.Event ev : events) {
                                                    boolean show = true;

                                                    if (categoryFilter != null && !categoryFilter.isEmpty()
                                                            && !ev.getCategory().equalsIgnoreCase(categoryFilter)) {
                                                        show = false;
                                                    }

                                                    if (monthYearFilter != null && !monthYearFilter.isEmpty()) {
                                                        String eventMonth = ev.getStartTime().substring(0, 7); // yyyy-MM
                                                        if (!eventMonth.equals(monthYearFilter)) {
                                                            show = false;
                                                        }
                                                    }

                                                    if (!show) {
                                                        continue;
                                                    }

                                                    int eventId = -1;
                                                    try {
                                                        eventId = Integer.parseInt(ev.getId());
                                                    } catch (Exception e) {
                                                        out.println("<tr><td colspan='8'>Invalid event ID for: " + ev.getTitle() + "</td></tr>");
                                                        continue;
                                                    }
                                            %>
                                            <tr>
                                                <td><%= eventCount++%></td>
                                                <td><%= ev.getTitle()%></td>
                                                <td><%= ev.getCategory()%></td>
                                                <td><%= ev.getStartTime()%></td>
                                                <td><%= ev.getEndTime()%></td>
                                                <td><%= ev.getCreatedBy()%></td>
                                                <td><%= ev.getPaymentAmount() > 0 ? String.format("%.2f", ev.getPaymentAmount()) : "-"%></td>
                                                <td>
                                                    <a href="eventParticipant.jsp?eventId=<%= eventId%>" class="btn btn-info btn-sm" target="_blank"><%= bundle.getString("view_participant")%></a>
                                                </td>
                                                <td>
                                                    <a href="<%= request.getContextPath()%>/GenerateOPRServlet?eventId=<%= eventId%>" 
                                                       target="_blank" class="btn btn-gradient-info"><%= bundle.getString("view_opr_button")%></a>
                                                </td>

                                            </tr>
                                            <% }
                                                if (eventCount == 1) {%>
                                            <tr><td colspan="8" class="text-center"><%= bundle.getString("no_event")%>.</td></tr>
                                            <% }%>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>



                        <!-- Back -->
                        <div class="mt-4">
                            <a href="hmdashboard.jsp" class="btn btn-secondary"><%= bundle.getString("no_parent")%></a>
                        </div>
                    </div>             <!-- content-wrapper ends -->
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
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
                                               function showTab(tabId) {
                                                   $(".report-tab").hide();
                                                   $("#" + tabId).show();
                                                   $(".nav-link").removeClass("active");
                                                   $("#" + tabId + "-tab").addClass("active");
                                               }

                                               $(document).ready(function () {
                                                   showTab("<%= request.getParameter("tab") != null ? request.getParameter("tab") : "parentTab"%>");
                                               });
        </script>
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


