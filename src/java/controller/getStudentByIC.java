package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;
import org.json.JSONObject;
import util.DBConfig;

@WebServlet("/getStudentByIC")
public class getStudentByIC extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ic = request.getParameter("ic");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();

        try (PrintWriter out = response.getWriter()) {

            if (ic == null || ic.trim().isEmpty()) {
                json.put("success", false);
                json.put("message", "IC number is missing.");
                out.print(json.toString());
                return;
            }

            ic = ic.trim().replace("-", ""); // Normalize input

            try (Connection conn = DBConfig.getConnection()) {

                // Debug (optional): List all students
                /*
                Statement debugStmt = conn.createStatement();
                ResultSet allStudents = debugStmt.executeQuery("SELECT student_name, ic_number FROM student");
                while (allStudents.next()) {
                    System.out.println(" - " + allStudents.getString("student_name") + ", IC: " + allStudents.getString("ic_number"));
                }
                */

                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT student_name FROM student WHERE REPLACE(ic_number, '-', '') = ?"
                );
                stmt.setString(1, ic);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("student_name"); // FIXED: Correct column name
                    json.put("success", true);
                    json.put("name", name);
                    json.put("ic", ic);
                } else {
                    json.put("success", false);
                    json.put("message", "No student found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                json.put("success", false);
                json.put("message", "Database error.");
            }

            out.print(json.toString());
            out.flush();
        }
    }
}
