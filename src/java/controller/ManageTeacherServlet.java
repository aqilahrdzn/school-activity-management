package controller;

import dao.TeacherDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ManageTeacherServlet")
public class ManageTeacherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        TeacherDAO teacherDAO = new TeacherDAO();

        if ("update".equals(action)) {
            try {
                int teacherId = Integer.parseInt(request.getParameter("teacherId"));
                String newClass = request.getParameter("kelas");
                String selectedYearStr = request.getParameter("selectedYear");
                int selectedYear = (selectedYearStr != null && !selectedYearStr.isEmpty())
                        ? Integer.parseInt(selectedYearStr)
                        : 2025; // default year

                // Validate input
                if (newClass == null || newClass.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Please select a class.&selectedYear=" + selectedYear);
                    return;
                }

                // Check if the class is already assigned to a different teacher
                boolean isAlreadyAssigned = teacherDAO.isClassAlreadyAssigned(newClass, selectedYear, teacherId);
                if (isAlreadyAssigned) {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Class already assigned to another teacher for " + selectedYear + ".&selectedYear=" + selectedYear);
                    return;
                }

                boolean success = teacherDAO.updateTeacherClass(teacherId, newClass, selectedYear);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?success=Class updated successfully!&selectedYear=" + selectedYear);
                } else {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Failed to update class.&selectedYear=" + selectedYear);
                }

            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Invalid input for teacher ID or year.");
            }

        } else if ("delete".equals(action)) {
            try {
                int teacherId = Integer.parseInt(request.getParameter("teacherId"));
                boolean success = teacherDAO.deleteTeacher(teacherId);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?success=Teacher deleted successfully!");
                } else {
                    response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Failed to delete teacher.");
                }

            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Invalid teacher ID.");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/clerk/teacherList.jsp?error=Invalid action specified.");
        }
    }
}
