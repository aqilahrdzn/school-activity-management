<%-- 
    Document   : createEvent
    Created on : May 8, 2025, 4:51:36 PM
    Author     : Lenovo
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="util.DBConfig"%>
<%@page import="java.sql.Connection"%>
<%@page import="dao.TeacherDAO"%>
<%@page import="model.Teacher"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.SQLException"%>
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
                <div class="main-panel">
                    <div class="content-wrapper">
                        <div class="page-header">
                            <h3 class="page-title"> <%= bundle.getString("parent_approval")%></h3>
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb">
                                    <li class="breadcrumb-item"><a href="#"><%= bundle.getString("forms")%></a></li>
                                    <li class="breadcrumb-item active" aria-current="page">Form elements</li>
                                </ol>
                            </nav>
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-md-6 grid-margin stretch-card">
                            <div class="card">
                                <div class="card-body">

                                    <h4 class="card-title"><%= bundle.getString("create_event_title")%></h4>
                                    <h2><%= bundle.getString("google_calendar_title")%></h2>
                                    <div class="iframe-container">
                                        <iframe 
                                            src="https://calendar.google.com/calendar/embed?src=aqilah031103060404%40gmail.com&ctz=Asia%2FKuala_Lumpur" 
                                            style="border:0; width: 1000px; height: 500px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);" 
                                            frameborder="0" 
                                            scrolling="no">
                                        </iframe>
                                    </div>

                                    <form class="forms-sample" action="<%= request.getContextPath()%>/EventController" method="post" onsubmit="return confirmSubmission()">
                                        <div class="form-group">
                                            <label for="category"><%= bundle.getString("event_category_label")%></label>
                                            <select id="event-category" name="event-category" onchange="toggleVenueField()">
                                                <option value=""><%= bundle.getString("select_category_placeholder")%></option>
                                                <option value="school"><%= bundle.getString("school_event")%></option>
                                                <option value="external"><%= bundle.getString("external_event")%></option>
                                                <option value="payment"><%= bundle.getString("payment_event")%></option>
                                            </select>
                                        </div>
                                        <div class="form-group" id="venue-section" style="display:none;">
                                            <label for="venue"><%= bundle.getString("venue_label")%>:</label>
                                            <input type="text" id="venue" name="venue" placeholder="<%= bundle.getString("venue_placeholder")%>">
                                        </div>

                                        <div class="form-group" id="payment-amount-section">
                                            <label for="paymentAmount"><%= bundle.getString("payment_amount_label")%>:</label>
                                            <input type="number" id="paymentAmount" name="paymentAmount" step="0.01" min="0" placeholder="<%= bundle.getString("payment_amount_placeholder")%>">
                                        </div>

                                        <div class="form-group">
                                            <label for="title"><%= bundle.getString("event_title_label")%>:</label>
                                            <input type="text" id="title" name="title" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="description"><%= bundle.getString("event_description_label")%>:</label>
                                            <textarea id="description" name="description" rows="4" cols="50"></textarea>
                                        </div>
                                        <div class="form-group">
                                            <label for="startTime"><%= bundle.getString("start_time_label")%>:</label>
                                            <input type="datetime-local" id="startTime" name="startTime" required min="<%= java.time.LocalDateTime.now().toString().substring(0, 16)%>">
                                            <small><%= bundle.getString("parent_approval")%>: YYYY-MM-DDTHH:mm (<%= bundle.getString("parent_approval")%>)</small>
                                        </div>

                                        <div class="form-group">
                                            <label for="endTime"><%= bundle.getString("end_time_label")%>:</label>
                                            <input type="datetime-local" id="endTime" name="endTime" required min="<%= java.time.LocalDateTime.now().toString().substring(0, 16)%>">
                                            <small><%= bundle.getString("parent_approval")%>: YYYY-MM-DDTHH:mm (<%= bundle.getString("parent_approval")%>)</small>
                                        </div>

                                        <div class="form-group">
                                            <label for="timeZone"><%= bundle.getString("time_zone_label")%>:</label>
                                            <input type="text" id="timeZone" name="timeZone" value="Asia/Kuala_Lumpur" required>
                                        </div>
                                        <div class="form-group">
                                            <input type="radio" id="selectByClass" name="selectType" value="class" onclick="toggleSelection()" checked>
                                            <label for="selectByClass"><%= bundle.getString("select_by_class")%></label>
                                        </div>
                                        <div class="form-group">
                                            <input type="radio" id="selectIndividually" name="selectType" value="individual" onclick="toggleSelection()">
                                            <label for="selectIndividually"><%= bundle.getString("select_individually")%></label>
                                        </div>
                                        <div class="form-group">
                                            <input type="radio" id="selectBySport" name="selectType" value="sport" onclick="toggleSelection()">
                                            <label for="selectBySport"><%= bundle.getString("select_by_sport")%></label>
                                        </div>
                                        <div class="form-group">
                                            <input type="radio" id="selectByUniform" name="selectType" value="uniform" onclick="toggleSelection()">
                                            <label for="selectByUniform"><%= bundle.getString("select_by_uniform")%></label>
                                        </div>
                                        <div id="class-selection" style="margin-top:10px;">
                                            <label><%= bundle.getString("choose_class")%>:</label>
                                            <div class="checkbox-grid">
                                                <label><input type="checkbox" name="classDropdown" value="1 Makkah"> 1 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="1 Madinah"> 1 Madinah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="2 Makkah"> 2 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="2 Madinah"> 2 Madinah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="3 Makkah"> 3 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="3 Madinah"> 3 Madinah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="4 Makkah"> 4 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="4 Madinah"> 4 Madinah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="5 Makkah"> 5 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="5 Madinah"> 5 Madinah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="6 Makkah"> 6 Makkah</label><br>
                                                <label><input type="checkbox" name="classDropdown" value="6 Madinah"> 6 Madinah</label>
                                            </div>
                                        </div>

                                        <div id="individual-selection" style="display:none; margin-top:10px;">
                                            <label for="icInput"><%= bundle.getString("ic_number")%>:</label>
                                            <input type="text" id="icInput" name="icInput" placeholder="<%= bundle.getString("student_ic_placeholder")%>">
                                            <button type="button" onclick="addStudentByIC()"><%= bundle.getString("add_student_button")%></button>
                                            <div id="selected-students" style="color: black">
                                                <p><%= bundle.getString("no_student_selected")%>.</p>
                                            </div>
                                        </div>

                                        <div id="sport-selection" style="display:none; margin-top:10px;">
                                            <label for="sportDropdown"><%= bundle.getString("choose_sport_team")%>:</label>
                                            <select id="sportDropdown" name="sportDropdown">
                                                <option value=""><%= bundle.getString("choose_sport_team")%></option>
                                                <option value="Red Team"><%= bundle.getString("red_team")%></option>
                                                <option value="Yellow Team"><%= bundle.getString("yellow_team")%></option>
                                                <option value="Green Team">G<%= bundle.getString("green_team")%></option>
                                                <option value="Blue Team"><%= bundle.getString("blue_team")%></option>
                                            </select>
                                        </div>

                                        <div id="uniform-selection" style="display:none; margin-top:10px;">
                                            <label for="uniformDropdown"><%= bundle.getString("choose_uniform_unit")%>:</label>
                                            <select id="uniformDropdown" name="uniformDropdown">
                                                <option value=""><%= bundle.getString("choose_uniform_unit")%></option>
                                                <option value="Tunas Puteri">Tunas Puteri</option>
                                                <option value="Puteri Islam">Puteri Islam</option>
                                                <option value="Tunas Kadet Remaja Sekolah">Tunas Kadet Remaja Sekolah</option>
                                                <option value="Pengakap">Pengakap</option>
                                            </select>
                                        </div>
                                        <script>
                                            function toggleSelection() {
                                                const selectedRadio = document.querySelector('input[name="selectType"]:checked');
                                                if (!selectedRadio)
                                                    return;

                                                const selectionType = selectedRadio.value;

                                                document.getElementById("class-selection").style.display = (selectionType === "class") ? "block" : "none";
                                                document.getElementById("individual-selection").style.display = (selectionType === "individual") ? "block" : "none";
                                                document.getElementById("sport-selection").style.display = (selectionType === "sport") ? "block" : "none";
                                                document.getElementById("uniform-selection").style.display = (selectionType === "uniform") ? "block" : "none";

                                                // Clear individual selection list when switching modes
                                                if (selectionType !== "individual") {
                                                    selectedStudents.clear();
                                                    document.getElementById("selected-students").innerHTML = "<p><%= bundle.getString("no_student_selected")%></p>";
                                                    // Remove all hidden IC inputs
                                                    document.querySelectorAll('.hidden-ic-input').forEach(input => input.remove());
                                                }
                                            }

                                            function togglePaymentField() {
                                                document.addEventListener("DOMContentLoaded", function () {
                                                    const categorySelect = document.getElementById("event-category");
                                                    const paymentSection = document.getElementById("payment-amount-section");

                                                    // Initially hide the payment amount section
                                                    paymentSection.style.display = "none";

                                                    // Listen for changes in the dropdown
                                                    categorySelect.addEventListener("change", function () {
                                                        if (categorySelect.value === "payment") {
                                                            paymentSection.style.display = "block";
                                                        } else {
                                                            paymentSection.style.display = "none";
                                                        }
                                                    });
                                                });
                                            }

                                            // Call the function here
                                            togglePaymentField();

                                            const selectedStudents = new Set(); // Stores ICs to prevent duplicates in UI

                                            function addStudentByIC() {
                                                const ic = document.getElementById("icInput").value.trim().replace(/-/g, ""); // Remove hyphens
                                                if (ic === "") {
                                                    alert("Please enter an IC number.");
                                                    return;
                                                }

                                                if (selectedStudents.has(ic)) {
                                                    alert("Student already added.");
                                                    return;
                                                }

                                                fetch('<%= request.getContextPath()%>/EventController?action=getStudentByIC&ic=' + encodeURIComponent(ic))
                                                        .then(response => {
                                                            if (!response.ok) {
                                                                if (response.status === 404 || (response.headers.get('Content-Type').includes('application/json') && response.status === 200)) {
                                                                    return response.json(); // Still try to parse JSON for message
                                                                }
                                                                throw new Error("Network response was not ok: " + response.statusText);
                                                            }
                                                            return response.json();
                                                        })
                                                        .then(data => {
                                                            console.log("DEBUG: Response from server:", data);

                                                            if (data && data.success && data.name && data.ic) {
                                                                updateStudentList(data.name, data.ic);
                                                            } else {
                                                                alert(data.message || "Student not found.");
                                                            }
                                                        })
                                                        .catch(error => {
                                                            console.error("Fetch error:", error);
                                                            alert("Error retrieving student info. Please check the IC number and try again.");
                                                        });
                                            }

                                            function updateStudentList(name, ic) {
                                                if (!name || !ic || selectedStudents.has(ic))
                                                    return;

                                                selectedStudents.add(ic);

                                                const listDiv = document.getElementById("selected-students");
                                                let ul = document.getElementById("student-ul");
                                                if (!ul) {
                                                    listDiv.innerHTML = "<strong>Selected Students:</strong><ul id='student-ul'></ul>";
                                                    ul = document.getElementById("student-ul");
                                                }

                                                const li = document.createElement("li");
                                                li.style.display = 'flex'; // Use flexbox for alignment
                                                li.style.alignItems = 'center';
                                                li.style.marginBottom = '5px';

                                                // UI Checkbox (for display and removal)
                                                const checkbox = document.createElement("input");
                                                checkbox.type = "checkbox";
                                                checkbox.name = "selected-students-ui"; // Different name to avoid direct submission
                                                checkbox.checked = true;
                                                checkbox.style.marginRight = '10px';
                                                checkbox.addEventListener('change', function () {
                                                    if (!this.checked) {
                                                        removeStudent(ic, li);
                                                    }
                                                });

                                                const textSpan = document.createElement("span");
                                                textSpan.textContent = "Name: " + name + ", IC: " + ic;

                                                li.appendChild(checkbox);
                                                li.appendChild(textSpan);
                                                ul.appendChild(li); // Append the list item to the UL

                                                // Hidden Input (for form submission)
                                                const hiddenInput = document.createElement("input");
                                                hiddenInput.type = "hidden";
                                                hiddenInput.name = "selectedICs"; // This name *will* be sent to the servlet
                                                hiddenInput.value = ic;
                                                hiddenInput.className = "hidden-ic-input"; // Add a class for easy selection
                                                listDiv.appendChild(hiddenInput); // Add to listDiv to be part of the form
                                            }

                                            function removeStudent(ic, listItem) {
                                                selectedStudents.delete(ic);
                                                listItem.remove(); // Remove from UI

                                                // Remove the corresponding hidden input
                                                const hiddenInputs = document.querySelectorAll(`.hidden-ic-input[value="${ic}"]`);
                                                hiddenInputs.forEach(input => input.remove());

                                                // If no students left, show "No students selected."
                                                const ul = document.getElementById("student-ul");
                                                if (!ul || ul.children.length === 0) {
                                                    document.getElementById("selected-students").innerHTML = "<p>No students selected.</p>";
                                                }
                                            }

                                            // Initialize toggleSelection on page load to set initial state
                                            document.addEventListener('DOMContentLoaded', toggleSelection);

                                            function confirmSubmission() {
                                                const category = document.getElementById("event-category").value;
                                                const title = document.getElementById("title").value;
                                                const description = document.getElementById("description").value;
                                                const startTime = document.getElementById("startTime").value;
                                                const endTime = document.getElementById("endTime").value;
                                                const timeZone = document.getElementById("timeZone").value;
                                                const paymentAmount = document.getElementById("paymentAmount").value;

                                                // Gather selected students
                                                const selectedStudentsArray = Array.from(document.querySelectorAll('input[name="selected-students-ui"]:checked'))
                                                        .map(input => input.nextSibling.textContent.trim());

                                                let confirmationMessage = "Please confirm the following details:\n\n";
                                                confirmationMessage += "Event Category: " + category + "\n";
                                                confirmationMessage += "Event Title: " + title + "\n";
                                                confirmationMessage += "Event Description: " + description + "\n";
                                                confirmationMessage += "Start Time: " + startTime + "\n";
                                                confirmationMessage += "End Time: " + endTime + "\n";
                                                confirmationMessage += "Time Zone: " + timeZone + "\n";
                                                confirmationMessage += "Payment Amount: RM " + paymentAmount + "\n";
                                                if (selectedStudentsArray.length > 0) {
                                                    confirmationMessage += "Selected Students: " + selectedStudentsArray.join(", ") + "\n";
                                                } else {
                                                    confirmationMessage += "No students selected.\n";
                                                }

                                                return confirm(confirmationMessage);
                                            }

                                            // Function to toggle Venue field visibility based on event category selection
                                            function toggleVenueField() {
                                                const category = document.getElementById('event-category').value;
                                                const venueSection = document.getElementById('venue-section');
                                                if (category === 'external' || category === 'payment') {
                                                    venueSection.style.display = 'block';
                                                } else {
                                                    venueSection.style.display = 'none';
                                                }
                                            }

                                            // Initialize toggleVenueField on page load
                                            document.addEventListener('DOMContentLoaded', toggleVenueField);
                                        </script>


                                        <button type="submit" class="btn btn-gradient-primary me-2"><%= bundle.getString("submit")%></button>
                                    </form>
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