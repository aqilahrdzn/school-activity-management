<%-- 
    Document   : mainpage
    Created on : Jun 17, 2025, 9:06:09 PM
    Author     : Lenovo
--%>

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
        <!-- Theme Made By www.w3schools.com - No Copyright -->
        <title>School Avtivity Management System</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
        <link href="https://fonts.googleapis.com/css?family=Lato" rel="stylesheet" type="text/css">
        <link href="https://fonts.googleapis.com/css?family=Montserrat" rel="stylesheet" type="text/css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
        <style>
            body {
                font: 400 15px/1.8 Lato, sans-serif;
                color: #777;
            }
            h3, h4 {
                margin: 10px 0 30px 0;
                letter-spacing: 10px;
                font-size: 20px;
                color: #111;
            }
            .container {
                padding: 80px 120px;
            }
            .person {
                border: 10px solid transparent;
                margin-bottom: 25px;
                width: 80%;
                height: 80%;
                opacity: 0.7;
            }
            .person:hover {
                border-color: #f1f1f1;
            }
            .carousel-inner img {
                width: 1400px;

                margin: auto;
            }
            .carousel-caption h3 {
                color: #fff !important;
            }
            @media (max-width: 600px) {
                .carousel-caption {
                    display: none; /* Hide the carousel text when the screen is less than 600 pixels wide */
                }
            }
            .bg-1 {
                background: #2d2d30;
                color: #bdbdbd;
            }
            .bg-1 h3 {
                color: #fff;
            }
            .bg-1 p {
                font-style: italic;
            }
            .list-group-item:first-child {
                border-top-right-radius: 0;
                border-top-left-radius: 0;
            }
            .list-group-item:last-child {
                border-bottom-right-radius: 0;
                border-bottom-left-radius: 0;
            }
            .thumbnail {
                padding: 0 0 15px 0;
                border: none;
                border-radius: 0;
            }
            .thumbnail p {
                margin-top: 15px;
                color: #555;
            }
            .btn {
                padding: 10px 20px;
                background-color: #333;
                color: #f1f1f1;
                border-radius: 0;
                transition: .2s;
            }
            .btn:hover, .btn:focus {
                border: 1px solid #333;
                background-color: #fff;
                color: #000;
            }
            .modal-header, h4, .close {
                background-color: #333;
                color: #fff !important;
                text-align: center;
                font-size: 30px;
            }
            .modal-header, .modal-body {
                padding: 40px 50px;
            }
            .nav-tabs li a {
                color: #777;
            }
            #googleMap {
                width: 100%;
                height: 400px;
                -webkit-filter: grayscale(100%);
                filter: grayscale(100%);
            }
            .navbar {
                font-family: Montserrat, sans-serif;
                margin-bottom: 0;
                background: linear-gradient(to right, #e0b3ff, #8e2de2);
                border: 0;
                font-size: 11px !important;
                letter-spacing: 4px;
                opacity: 0.9;
            }
            .navbar li a, .navbar .navbar-brand {
                color: #000000 !important;
            }


            .navbar-nav li a:hover {
                color: #222 !important; /* dark gray/black */
            }

            .navbar-nav li.active a {
                color: #fff !important;
                background-color: #29292c !important;
            }
            .navbar-default .navbar-toggle {
                border-color: transparent;
            }
            .open .dropdown-toggle {
                color: #fff;
                background-color: #555 !important;
            }
            .dropdown-menu li a {
                color: #000 !important;
            }
            .dropdown-menu li a:hover {
                background-color: red !important;
            }
            footer {
                background: linear-gradient(to right, #e0b3ff, #8e2de2);
                color: #f5f5f5;
                padding: 32px;
            }
            footer a {
                color: #f5f5f5;
            }
            footer a:hover {
                color: #777;
                text-decoration: none;
            }
            .form-control {
                border-radius: 0;
            }
            textarea {
                resize: none;
            }
        </style>
    </head>
    <body id="myPage" data-spy="scroll" data-target=".navbar" data-offset="50">

        <nav class="navbar navbar-default navbar-fixed-top">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>                        
                    </button>
                    <a class="navbar-brand" href="#myPage">Logo</a>
                </div>
                <div class="collapse navbar-collapse" id="myNavbar">
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="login.jsp"><%= bundle.getString("login")%></a></li>
                        <li>
                            <form method="get" action="" style="margin-top:8px;">
                                <select name="lang" onchange="this.form.submit()" style="padding:3px;">
                                    <option value="en" <%= "en".equals(currentLang) ? "selected" : ""%>>EN</option>
                                    <option value="ms" <%= "ms".equals(currentLang) ? "selected" : ""%>>BM</option>
                                </select>
                            </form>
                        </li>
                    </ul>

                </div>
            </div>
        </nav>

        <div id="myCarousel" class="carousel slide" data-ride="carousel">
            <!-- Indicators -->
            <ol class="carousel-indicators">
                <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
                <li data-target="#myCarousel" data-slide-to="1"></li>
                <li data-target="#myCarousel" data-slide-to="2"></li>
            </ol>

            <!-- Wrapper for slides -->
            <div class="carousel-inner" role="listbox">
                <div class="item active">
                    <img src="assets/images/skkj3.jpg" alt="New York" >
                    <div class="carousel-caption">
                        <h3><%= bundle.getString("welcome_to")%> SK Kerayong Jaya</h3>

                        <p>Disini Pemimpin Dilahirkan</p>
                    </div>      
                </div>

                <div class="item">
                    <img src="assets/images/skkj 1.jpg" alt="Chicago" >
                    <div class="carousel-caption">
                        <h3>Welcome to SK Kerayong Jaya</h3>
                        <p>Disini Pemimpin Dilahirkan</p>
                    </div>      
                </div>

                <div class="item">
                    <img src="assets/images/skkj2.jpg" alt="Los Angeles" width="1200" height="400">
                    <div class="carousel-caption">
                        <h3>Welcome to SK Kerayong Jaya</h3>
                        <p>Disini Pemimpin Dilahirkan</p>
                    </div>      
                </div>
            </div>

            <!-- Left and right controls -->
            <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>

        <!-- Container (The Band Section) -->
        <div id="band" class="container text-center">
            <h3>SEKOLAH KEBANGSAAN KERAYONG JAYA</h3>
            <p><em><%= bundle.getString("about")%></em></p>
            <p><%= bundle.getString("skkj_desc")%></p> <br>
            <div class="row">
                <div class="col-sm-4">
                    <p class="text-center"><strong>Guru Besar</strong></p><br>
                    <a href="#demo" data-toggle="collapse">
                        <img src="assets/images/gb skkj.jpg" class="img-circle person" alt="Random Name" width="255" height="255">
                    </a>
                    <div id="demo" class="collapse">
                        <p>Pn.Mazita Binti Rosli</p>
                        
                    </div>
                </div>
                <div class="col-sm-4">
                    <p class="text-center"><strong>Guru Penolong Kanan</strong></p><br>
                    <a href="#demo2" data-toggle="collapse">
                        <img src="assets/images/pk ko skkj.jpg" class="img-circle person" alt="Random Name" width="255" height="255">
                    </a>
                    <div id="demo2" class="collapse">
                        <p>En.Ibrahim Bin Ismail</p>
                        
                    </div>
                </div>
                <div class="col-sm-4">
                    <p class="text-center"><strong>Guru Penolong Kanan Hal Ehwal Murid</strong></p><br>
                    <a href="#demo3" data-toggle="collapse">
                        <img src="assets/images/pk1 skkj.jpg" class="img-circle person" alt="Random Name" width="255" height="255">
                    </a>
                    <div id="demo3" class="collapse">
                        <p>En.Nasaruddin Bin Bakar</p>
                        
                    </div>
                </div>
            </div>
        </div>



        <!-- Footer -->
        <footer class="footer">
            <div class="d-sm-flex justify-content-center justify-content-sm-between">
                <span class="text-muted text-center text-sm-left d-block d-sm-inline-block">Copyright © 2023 <a href="https://www.bootstrapdash.com/" target="_blank">BootstrapDash</a>. All rights reserved.</span>
                <span class="float-none float-sm-right d-block mt-1 mt-sm-0 text-center">Hand-crafted & made with <i class="mdi mdi-heart text-danger"></i></span>
            </div>
        </footer>

        <script>
            $(document).ready(function () {
                // Initialize Tooltip
                $('[data-toggle="tooltip"]').tooltip();

                // Add smooth scrolling to all links in navbar + footer link
                $(".navbar a, footer a[href='#myPage']").on('click', function (event) {

                    // Make sure this.hash has a value before overriding default behavior
                    if (this.hash !== "") {

                        // Prevent default anchor click behavior
                        event.preventDefault();

                        // Store hash
                        var hash = this.hash;

                        // Using jQuery's animate() method to add smooth page scroll
                        // The optional number (900) specifies the number of milliseconds it takes to scroll to the specified area
                        $('html, body').animate({
                            scrollTop: $(hash).offset().top
                        }, 900, function () {

                            // Add hash (#) to URL when done scrolling (default click behavior)
                            window.location.hash = hash;
                        });
                    } // End if
                });
            })
        </script>

    </body>
</html>
