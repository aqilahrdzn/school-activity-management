package controller;

import dao.StudentDAO;
import model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ParentAuthServlet")
public class ParentAuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get IC numbers from form
        String[] icNumbers = request.getParameterValues("ic");

        if (icNumbers == null || icNumbers.length == 0) {
            response.getWriter().println("<h3>No IC numbers provided!</h3>");
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        List<Student> children = new ArrayList<>();

        // Check each IC in database
        for (String ic : icNumbers) {
            Student student = studentDAO.getChildDetailsByIC(ic);
            if (student != null) {
                children.add(student);
            } else {
                response.getWriter().println("<h3>Student with IC " + ic + " not found in the database.</h3>");
                return;
            }
        }

        // Save to session and redirect
        request.getSession().setAttribute("children", children);
        response.sendRedirect(request.getContextPath() + "/parent/registerParent.jsp");
    }
}
