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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Handle logout
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false); // Don't create new session
            if (session != null) {
                session.invalidate(); // Destroy session
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Redirect to login page for any non-POST access
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

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

                    session.setAttribute("isGuruKelas", teacher.getIsGuruKelas());
                    session.setAttribute("teacherClass", teacher.getKelas());
                } else {
                    System.out.println("[LoginServlet] Teacher details not found for: " + email);
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=teacherNotFound");
                    return;
                }

            } else if (role.equalsIgnoreCase("parent")) {
                ParentDAO parentDAO = new ParentDAO();
                Parent parent = parentDAO.getParentByEmail(email);

                if (parent != null) {
                    if (password.equals(parent.getPassword())) {
                        session.setAttribute("parent", parent);
                    } else {
                        System.out.println("[LoginServlet] Incorrect parent password.");
                        response.sendRedirect(request.getContextPath() + "/login.jsp?error=wrongPassword");
                        return;
                    }
                } else {
                    System.out.println("[LoginServlet] Parent details not found.");
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=parentNotFound");
                    return;
                }
            }

            // ✅ Redirect to dashboard based on role
            switch (role.toLowerCase()) {
                case "teacher":
                    response.sendRedirect(request.getContextPath() + "/teacher/teacherdashboard.jsp");
                    break;
                case "schoolclerk":
                    response.sendRedirect(request.getContextPath() + "/clerk/clerkdashboard.jsp");
                    break;
                case "headmaster":
                    response.sendRedirect(request.getContextPath() + "/headmaster/hmdashboard.jsp");
                    break;
                case "parent":
                    response.sendRedirect(request.getContextPath() + "/parent/parentdashboard.jsp");
                    break;
                default:
                    System.out.println("[LoginServlet] Unknown role.");
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalidRole");
                    break;
            }

        } else {
            System.out.println("[LoginServlet] Invalid credentials for: " + email);
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalidCredentials");
        }
    }
}
