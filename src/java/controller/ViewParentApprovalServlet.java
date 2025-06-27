package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.DBConfig;

@WebServlet("/ViewParentApprovalServlet")
public class ViewParentApprovalServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Map<String, String>> approvalList = new ArrayList<>();
        String teacherEmail = (String) request.getSession().getAttribute("email");

        System.out.println("Teacher Email: " + teacherEmail);

        try (Connection con = DBConfig.getConnection()) {
            String sql = "SELECT p.name AS parent_name, p.email, p.contact_number, " +
                         "s.student_name, s.ic_number, e.title AS event_title, e.start_time, e.end_time, " +
                         "pa.status, pa.reason, pa.approved_at " +
                         "FROM parent_approval pa " +
                         "JOIN parent p ON pa.parent_id = p.id " +
                         "JOIN events e ON pa.event_id = e.id " +
                         "JOIN student s ON s.parent_id = p.id " +
                         "WHERE e.created_by = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, teacherEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> data = new HashMap<>();
                data.put("parent_name", rs.getString("parent_name"));
                data.put("email", rs.getString("email"));
                data.put("contact_number", rs.getString("contact_number"));
                data.put("student_name", rs.getString("student_name"));
                data.put("ic_number", rs.getString("ic_number"));
                data.put("event_title", rs.getString("event_title"));
                data.put("start_time", rs.getString("start_time"));
                data.put("end_time", rs.getString("end_time"));
                data.put("status", rs.getString("status"));
                data.put("reason", rs.getString("reason"));
                data.put("approved_at", rs.getString("approved_at"));

                approvalList.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ SAFELY FORWARD to JSP with context path
        request.getRequestDispatcher(request.getContextPath() + "/teacher/parentApprovals.jsp").forward(request, response);
    }
}
