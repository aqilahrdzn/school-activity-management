<%--
    Document   : editParent
    Created on : Jun 25, 2025, 9:45:00 PM
    Author     : Lenovo
--%>

<%@page import="model.Parent"%>
<%@page import="dao.ParentDAO"%>
<%@page import="model.Student"%>
<%@page import="dao.StudentDAO"%>
<%@page import="model.Teacher"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- ================================================================== --%>
<%--                   FETCH TEACHER, STUDENT, AND PARENT DATA                  --%>
<%-- ================================================================== --%>
<%
    // Retrieve teacher from session
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    if (teacher == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?errorMessage=Please log in first.");
        return;
    }

    // Get student IC from request parameter
    String studentIc = request.getParameter("studentIc");
    if (studentIc == null || studentIc.trim().isEmpty()) {
        response.sendRedirect("studentList.jsp?errorMessage=Student IC not provided.");
        return;
    }

    // Fetch student to display their name and verify class
    StudentDAO studentDAO = new StudentDAO();
    Student student = studentDAO.getStudentByIC(studentIc);
    if (student == null) {
        response.sendRedirect("studentList.jsp?errorMessage=Associated student not found.");
        return;
    }
    
    // Authorization check
    boolean isCurrentUserGuruKelas = "Yes".equals(teacher.getIsGuruKelas());
    if (!isCurrentUserGuruKelas || !teacher.getKelas().equals(student.getStudentClass())) {
        response.sendRedirect("studentList.jsp?errorMessage=You are not authorized to edit parent details for this student.");
        return;
    }

    // Fetch parent details using the new required DAO method
    ParentDAO parentDAO = new ParentDAO();
    Parent parent = parentDAO.getParentByStudentIc(studentIc);
    
    boolean isNewParent = false;
    if (parent == null) {
        parent = new Parent(); // Create an empty object for the form
        isNewParent = true;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Edit Parent Details - Purple Admin</title>
    <link rel="stylesheet" href="../assets/vendors/mdi/css/materialdesignicons.min.css">
    <link rel="stylesheet" href="../assets/vendors/css/vendor.bundle.base.css">
    <link rel="stylesheet" href="../assets/css/style.css">
    <link rel="shortcut icon" href="../assets/images/favicon.png" />
</head>
<body>
<div class="container-scroller">
    
    <div class="container-fluid page-body-wrapper">
        

        <div class="main-panel">
            <div class="content-wrapper">
                <div class="page-header">
                    <h3 class="page-title"> Parent/Guardian Details </h3>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="studentList.jsp">Student List</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Parent Details</li>
                        </ol>
                    </nav>
                </div>
                <div class="row">
                    <div class="col-12 grid-margin stretch-card">
                        <div class="card">
                            <div class="card-body">
                                <h4 class="card-title">Parent/Guardian of: <%= student.getStudentName() %> (<%= student.getIcNumber() %>)</h4>
                                <p class="card-description">
                                    <%= isNewParent ? "No parent record found. Enter details to create and link a new parent." : "Update the parent/guardian's information." %>
                                </p>
                                
                                <%-- This form submits to a servlet that can handle both creating and updating a parent --%>
                                <form class="forms-sample" action="<%= request.getContextPath() %>/UpdateParentServlet" method="post">
                                    
                                    <input type="hidden" name="studentIc" value="<%= studentIc %>">
                                    <input type="hidden" name="parentId" value="<%= parent.getId() %>">
                                    <input type="hidden" name="isNew" value="<%= isNewParent %>">

                                    <div class="form-group">
                                        <label for="name">Parent/Guardian Name</label>
                                        <input type="text" class="form-control" id="name" name="name" value="<%= parent.getName() != null ? parent.getName() : "" %>" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="icNumber">Parent/Guardian IC Number</label>
                                        <input type="text" class="form-control" id="icNumber" name="icNumber" value="<%= parent.getIcNumber() != null ? parent.getIcNumber() : "" %>" readonly>
                                    </div>
                                     <div class="form-group">
                                        <label for="email">Email Address</label>
                                        <input type="email" class="form-control" id="email" name="email" value="<%= parent.getEmail() != null ? parent.getEmail() : "" %>" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="contactNumber">Phone Number</label>
                                        <input type="tel" class="form-control" id="contactNumber" name="contactNumber" value="<%= parent.getContactNumber() != null ? parent.getContactNumber() : "" %>" required>
                                    </div>
                                    
                                    <button type="submit" class="btn btn-primary me-2">Save Details</button>
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