<%--
    Document   : updateStudent
    Created on : Jun 25, 2025, 9:45:00 PM
    Author     : Lenovo
--%>

<%@page import="model.Student"%>
<%@page import="dao.StudentDAO"%>
<%@page import="model.Teacher"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- ================================================================== --%>
<%--                        FETCH TEACHER AND STUDENT DATA                      --%>
<%-- ================================================================== --%>
<%
    // Retrieve teacher from session
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    if (teacher == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?errorMessage=Please log in first.");
        return;
    }

    // Get student IC from request parameter
    String studentIc = request.getParameter("ic");
    if (studentIc == null || studentIc.trim().isEmpty()) {
        response.sendRedirect("studentList.jsp?errorMessage=Student IC not provided.");
        return;
    }

    // Fetch student details from database using the provided DAO method
    StudentDAO studentDAO = new StudentDAO();
    Student student = studentDAO.getStudentByIC(studentIc);

    if (student == null) {
        response.sendRedirect("studentList.jsp?errorMessage=Student not found.");
        return;
    }
    
    // Authorization: Only the Guru Kelas of this student can update
    boolean isCurrentUserGuruKelas = "Yes".equals(teacher.getIsGuruKelas());
    String currentUserKelas = teacher.getKelas();

    if (!isCurrentUserGuruKelas || !currentUserKelas.equals(student.getStudentClass())) {
        response.sendRedirect("studentList.jsp?errorMessage=You are not authorized to update this student.");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Update Student - Purple Admin</title>
    <link rel="stylesheet" href="../assets/vendors/mdi/css/materialdesignicons.min.css">
    <link rel="stylesheet" href="../assets/vendors/css/vendor.bundle.base.css">
    <link rel="stylesheet" href="../assets/css/style.css">
    <link rel="shortcut icon" href="../assets/images/favicon.png" />
</head>
<body>
<div class="container-scroller">
    <%-- Assuming a standard navbar partial --%>
    

    <div class="container-fluid page-body-wrapper">
        

        <div class="main-panel">
            <div class="content-wrapper">
                <div class="page-header">
                    <h3 class="page-title"> Update Student Information </h3>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="studentList.jsp">Student List</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Update Student</li>
                        </ol>
                    </nav>
                </div>
                <div class="row">
                    <div class="col-12 grid-margin stretch-card">
                        <div class="card">
                            <div class="card-body">
                                <h4 class="card-title">Editing Student: <%= student.getStudentName() %></h4>
                                <p class="card-description"> Update the form below with the student's correct details. </p>
                                
                                <%-- This form should submit to a servlet (e.g., UpdateStudentServlet) --%>
                                <%-- The servlet will read these parameters, create a Student object, and call studentDAO.updateStudent(student) --%>
                                <form class="forms-sample" action="<%= request.getContextPath() %>/UpdateStudentServlet" method="post">
                                    
                                    <input type="hidden" name="id" value="<%= student.getId() %>">

                                    <div class="form-group">
                                        <label for="studentName">Student Name</label>
                                        <input type="text" class="form-control" id="studentName" name="studentName" value="<%= student.getStudentName() %>" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="icNumber">IC Number</label>
                                        <input type="text" class="form-control" id="icNumber" name="icNumber" value="<%= student.getIcNumber() %>" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="studentClass">Class</label>
                                        <select class="form-control" id="studentClass" name="studentClass" required>
                                            <% 
                                                String currentStudentClass = student.getStudentClass();
                                                String currentYear = currentStudentClass.substring(0, 1);
                                                String[] classesInYear = {currentYear + " Makkah", currentYear + " Madinah"};
                                                for (String cls : classesInYear) {
                                                    String selected = cls.equals(currentStudentClass) ? "selected" : "";
                                            %>
                                            <option value="<%= cls %>" <%= selected %>><%= cls %></option>
                                            <% } %>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="sportTeam">Sport Team</label>
                                        <input type="text" class="form-control" id="sportTeam" name="sportTeam" value="<%= student.getSportTeam() %>" readonly=>
                                    </div>
                                    <div class="form-group">
                                        <label for="uniformUnit">Uniform Unit</label>
                                        <input type="text" class="form-control" id="uniformUnit" name="uniformUnit" value="<%= student.getUniformUnit() %>" readonly>
                                    </div>
                                    
                                    <button type="submit" class="btn btn-primary me-2">Update Student</button>
                                    <a href="studentList.jsp" class="btn btn-light">Cancel</a>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
        </div>
    </div>
</div>
<script src="../assets/vendors/js/vendor.bundle.base.js"></script>
<script src="../assets/js/off-canvas.js"></script>
<script src="../assets/js/misc.js"></script>
</body>
</html>