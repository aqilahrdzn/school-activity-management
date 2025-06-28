package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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
                         "s.student_name, s.ic_number, s.class AS class, " +
                         "e.title AS event_title, e.start_time, e.end_time, " +
                         "pa.status, pa.reason, pa.approved_at, pa.disqualified, pa.parent_id, pa.event_id, pa.resit_file " +
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
                data.put("student_ic", rs.getString("ic_number")); // renamed for JSP clarity
                data.put("class", rs.getString("class"));
                data.put("event_title", rs.getString("event_title"));
                data.put("start_time", rs.getString("start_time"));
                data.put("end_time", rs.getString("end_time"));
                data.put("status", rs.getString("status"));
                data.put("reason", rs.getString("reason"));
                data.put("approved_at", rs.getString("approved_at"));
                data.put("disqualified", rs.getString("disqualified"));
                data.put("parent_id", rs.getString("parent_id"));
                data.put("event_id", rs.getString("event_id"));
                data.put("resit_file", rs.getString("resit_file"));

                approvalList.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to retrieve approval list: " + e.getMessage());
        }

        request.setAttribute("approvalList", approvalList);
        request.getRequestDispatcher("/teacher/parentApprovals.jsp").forward(request, response);
    }
}
