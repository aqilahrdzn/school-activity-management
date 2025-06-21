package controller;

import dao.ParentDAO;
import dao.StudentDAO;
import model.Parent;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import model.Student;

@WebServlet("/ParentRegisterServlet")
public class ParentRegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve parent details
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contactNumber = request.getParameter("contact_number");
        String icNumber = request.getParameter("ic_number");
        String[] childIcNumbers = request.getParameterValues("child_ic_numbers");

        Parent parent = new Parent(name, email, password, contactNumber, icNumber);
        ParentDAO parentDAO = new ParentDAO();

        // Insert parent and get generated ID
        int parentId = parentDAO.insertParentAndGetId(parent);
        if (parentId > 0 && childIcNumbers != null) {
            // Link children to the parent
            StudentDAO studentDAO = new StudentDAO();
            for (String childIc : childIcNumbers) {
                studentDAO.linkChildToParent(childIc, parentId);
            }
            response.sendRedirect("login.jsp"); // Success
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            response.sendRedirect(request.getContextPath() + "/parent/registerParent.jsp");
        }
    }
}
