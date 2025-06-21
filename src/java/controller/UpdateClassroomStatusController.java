package controller;

import dao.ClassroomDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/UpdateClassroomStatusController")
public class UpdateClassroomStatusController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int classroomId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");

        ClassroomDAO dao = new ClassroomDAO();

        try {
            dao.updateClassroomStatus(classroomId, status);
            response.sendRedirect("update_class.jsp?updated=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("update_class.jsp?updated=false");
        }
    }
}
