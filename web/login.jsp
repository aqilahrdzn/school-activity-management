<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.sql.*" %>

<%
    String lang = request.getParameter("lang");
    if (lang != null) {
        session.setAttribute("lang", lang);
    }
    String currentLang = (String) session.getAttribute("lang");
    if (currentLang == null) currentLang = "ms"; // Default: BM

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
        <link rel="stylesheet" href="assets/vendors/mdi/css/materialdesignicons.min.css">
        <link rel="stylesheet" href="assets/vendors/ti-icons/css/themify-icons.css">
        <link rel="stylesheet" href="assets/vendors/css/vendor.bundle.base.css">
        <link rel="stylesheet" href="assets/vendors/font-awesome/css/font-awesome.min.css">
        <!-- endinject -->
        <!-- Plugin css for this page -->
        <!-- End plugin css for this page -->
        <!-- inject:css -->
        <!-- endinject -->
        <!-- Layout styles -->
        <link rel="stylesheet" href="assets/css/style.css">
        <!-- End layout styles -->
        <link rel="shortcut icon" href="assets/images/favicon.png" />
    </head>
    <body>
        <div class="container-scroller">
            <div class="container-fluid page-body-wrapper full-page-wrapper">
                <div class="content-wrapper d-flex align-items-center auth">
                    <div class="row flex-grow">
                        <div class="col-lg-4 mx-auto">
                            <div class="auth-form-light text-left p-5">
                                <div class="brand-logo">
                                    <img src="assets/images/skkj logo.jpg">
                                </div>
                                <h4>Sekolah Kebangsaan Kerayong Jaya</h4>
                                <h6 class="font-weight-light"><%= bundle.getString("sign_in_continue") %></h6>

                                <%--
                                    This is the part where we add the alert.
                                    We check if the "errorMessage" attribute exists in the request scope.
                                    If it does, we use JavaScript to display an alert.
                                --%>
                                <%
                                    String errorMessage = (String) request.getAttribute("errorMessage");
                                    if (errorMessage != null && !errorMessage.isEmpty()) {
                                %>
                                <script type="text/javascript">
                                    // Use setTimeout to ensure the alert fires after the page is rendered
                                    setTimeout(function () {
                                        alert("<%= errorMessage%>");
                                    }, 100); // A small delay is often good practice
                                </script>
                                <%
                                    }
                                %>

                                <form class="pt-3" action="<%= request.getContextPath() %>/LoginServlet" method="post">

                                    <div class="form-group">
                                        <input type="email" class="form-control form-control-lg" id="email" name="email" placeholder="<%= bundle.getString("email_placeholder") %>">
                                    </div>
                                    <div class="form-group">
                                        <input type="password" class="form-control form-control-lg" id="password" name="password" placeholder="<%= bundle.getString("password_placeholder") %>">
                                    </div>
                                    <div class="mt-3 d-grid gap-2">
                                        <button class="btn btn-block btn-gradient-primary btn-lg font-weight-medium auth-form-btn" type="submit"><%= bundle.getString("login_button") %></button>
                                    </div>
                                    <div class="my-2 d-flex justify-content-between align-items-center">

                                    <div class="text-center mt-4 font-weight-light"><%= bundle.getString("no_account") %><a href="parent/parentAuth.jsp" class="text-primary"><%= bundle.getString("create_account") %></a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- container-scroller -->
        <!-- plugins:js -->
        <script src="assets/vendors/js/vendor.bundle.base.js"></script>
        <!-- endinject -->
        <!-- Plugin js for this page -->
        <!-- End plugin js for this page -->
        <!-- inject:js -->
        <script src="assets/js/off-canvas.js"></script>
        <script src="assets/js/misc.js"></script>
        <script src="assets/js/settings.js"></script>
        <script src="assets/js/todolist.js"></script>
        <script src="assets/js/jquery.cookie.js"></script>
        <!-- endinject -->
    </body>
</html>