<%@page import="model.Student"%>
<%@page import="java.util.List"%>
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
        <!-- End plugin css for this page -->
        <!-- inject:css -->
        <!-- endinject -->
        <!-- Layout styles -->
        <link rel="stylesheet" href="../assets/css/style.css">
        <!-- End layout styles -->
        <link rel="shortcut icon" href="../assets/images/favicon.png" />
    </head>
    <body>
        <script>
                            function addChildForm() {
                                const container = document.createElement("div");
                                container.classList.add("child-form");

                                container.innerHTML = `
        <label for="ic">IC number:</label>
        <input type="text" name="ic" required><br>
    `;

                                document.getElementById("form-container").appendChild(container);
                            }

                        </script>
        <div class="container-scroller">
            <div class="container-fluid page-body-wrapper full-page-wrapper">
                <div class="content-wrapper d-flex align-items-center auth">
                    <div class="row flex-grow">
                        <div class="col-lg-4 mx-auto">
                            <div class="auth-form-light text-left p-5">
                                <div class="brand-logo">
                                    <img src="../../assets/images/logo.svg">
                                </div>
                                <h4>New here?</h4>
                                <h6 class="font-weight-light">Signing up is easy. It only takes a few steps</h6>
                                <form class="forms-sample" method="post" action="../ParentRegisterServlet">
                                    <div class="form-group">
                                        <label for="exampleInputUsername1">Name:</label>
                                        <input type="text" class="form-control" id="exampleInputUsername1" name="name" placeholder="Name">
                                    </div>
                                    <div class="form-group">
                                        <label for="exampleInputEmail1">Email:</label>
                                        <input type="email" class="form-control" id="exampleInputEmail1" name="email" placeholder="Email">
                                    </div>
                                    <div class="form-group">
                                        <label for="exampleInputPassword1">Password:</label>
                                        <input type="password" class="form-control" id="exampleInputPassword1" name="password" placeholder="Password">
                                    </div>
                                    <div class="form-group">
                                        <label for="contactNumber">Contact Number:</label>
                                        <input type="text" class="form-control" id="contactNumber" name="contact_number" placeholder="Contact Number">
                                    </div>
                                    <div class="form-group">
                                        <label for="icNumber">IC Number:</label>
                                        <input type="text" class="form-control" id="icNumber" name="ic_number" placeholder="IC Number">
                                    </div>

                                    <!-- Children details as is -->
                                    <h3>Children Details</h3>
                                    <div>
                                        <%
                                            List<Student> children = (List<Student>) session.getAttribute("children");
                                            if (children != null && !children.isEmpty()) {
                                                for (Student child : children) {
                                        %>
                                        <p><strong>Child Name:</strong> <%= child.getStudentName()%></p>
                                        <p><strong>Class:</strong> <%= child.getStudentClass()%></p>
                                        <input type="hidden" name="child_ic_numbers" value="<%= child.getIcNumber()%>">
                                        <%
                                            }
                                        } else {
                                        %>
                                        <p>No children found. Please ensure your child is registered by their teacher.</p>
                                        <%
                                            }
                                        %>
                                    </div>

                                    <button type="submit" class="btn btn-gradient-primary me-2">Register</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- content-wrapper ends -->
            </div>
            <!-- page-body-wrapper ends -->
        </div>
        <!-- container-scroller -->
        <!-- plugins:js -->
        <script src="../../assets/vendors/js/vendor.bundle.base.js"></script>
        <!-- endinject -->
        <!-- Plugin js for this page -->
        <!-- End plugin js for this page -->
        <!-- inject:js -->
        <script src="../assets/js/off-canvas.js"></script>
        <script src="../assets/js/misc.js"></script>
        <script src="../assets/js/settings.js"></script>
        <script src="../assets/js/todolist.js"></script>
        <script src="../assets/js/jquery.cookie.js"></script>
        <!-- endinject -->
    </body>
</html>