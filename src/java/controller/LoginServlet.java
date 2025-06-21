package controller;

import dao.LoginDAO;
import dao.ParentDAO;
import dao.TeacherDAO;
import model.Teacher;
import model.Parent;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();

        System.out.println("[LoginServlet] Attempting login for email: " + email);

        LoginDAO loginDAO = new LoginDAO();
        String role = loginDAO.validateUser(email, password); // returns role if valid

        System.out.println("[LoginServlet] Detected role: " + role);

        if (role != null) {
            HttpSession session = request.getSession();
            session.setAttribute("email", email);
            session.setAttribute("role", role.toLowerCase()); // normalize

            if (role.equalsIgnoreCase("teacher")
                    || role.equalsIgnoreCase("headmaster")
                    || role.equalsIgnoreCase("schoolclerk")) {

                TeacherDAO teacherDAO = new TeacherDAO();
                Teacher teacher = teacherDAO.getTeacherDetails(email);

                if (teacher != null) {
                    System.out.println("[LoginServlet] Logged in as: " + teacher.getName());

                    session.setAttribute("teacher", teacher);
                    session.setAttribute("emailSender", teacher.getEmail());
                    session.setAttribute("emailPassword", "epvajwpbzvaixplv");

                    session.setAttribute("isGuruKelas", teacher.getIsGuruKelas()); // Corrected
                    session.setAttribute("teacherClass", teacher.getKelas());

                } else {
                    request.setAttribute("errorMessage", "Teacher details not found.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }
            } else if (role.equalsIgnoreCase("parent")) {
                ParentDAO parentDAO = new ParentDAO();
                Parent parent = parentDAO.getParentByEmail(email);

                if (parent != null) {
                    // Compare entered password with DB password
                    if (password.equals(parent.getPassword())) {
                        session.setAttribute("parent", parent); // ✅ Password now included in session
                    } else {
                        request.setAttribute("errorMessage", "Incorrect password.");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                        return;
                    }
                } else {
                    request.setAttribute("errorMessage", "Parent details not found.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }
            }

            // Redirect to dashboard based on role
            switch (role.toLowerCase()) {
                case "teacher":
                    response.sendRedirect("teacher/teacherdashboard.jsp");
                    break;
                case "schoolclerk":
                    response.sendRedirect("clerk/clerkdashboard.jsp");
                    break;
                case "headmaster":
                    response.sendRedirect("headmaster/hmdashboard.jsp");
                    break;
                case "parent":
                    response.sendRedirect("parent/parentdashboard.jsp");
                    break;
                default:
                    request.setAttribute("errorMessage", "Invalid role assigned.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            System.out.println("[LoginServlet] Invalid credentials for: " + email);
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
