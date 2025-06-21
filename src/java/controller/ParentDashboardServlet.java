package controller;

import dao.ParentDAO;
import model.Parent;
import model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ParentDashboardServlet")
public class ParentDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Retrieve email from session
        String email = (String) session.getAttribute("email");

        if (email == null || email.isEmpty()) {
            // Redirect to login if email is not in session
            System.err.println("No email found in session. Redirecting to login page.");
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("Email retrieved from session: " + email);

        ParentDAO parentDAO = new ParentDAO();
        Parent parent = parentDAO.getParentByEmail(email);

        if (parent != null) {
            System.out.println("Parent details found for email: " + email);

            // Fetch children details using JOIN
            List<Student> children = parentDAO.getChildrenByParentEmail(email);
            if (children.isEmpty()) {
                System.err.println("No children found for parent with email: " + email);
            } else {
                System.out.println("Children details retrieved for parent: " + email);
            }

            // Set attributes to pass to JSP
            request.setAttribute("parent", parent);
            request.setAttribute("children", children);
        } else {
            System.err.println("No parent details found for email: " + email);
            request.setAttribute("errorMessage", "Parent details not found.");
        }

        // Forward to JSP
        request.getRequestDispatcher("parentDashboard.jsp").forward(request, response);
    }
}
