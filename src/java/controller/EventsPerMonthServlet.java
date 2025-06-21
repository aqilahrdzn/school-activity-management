package controller;

import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;
import java.io.IOException;
import util.DBConfig;

@WebServlet("/events-per-month")

public class EventsPerMonthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int year = 2025; // default year
        String yearParam = request.getParameter("year");
        if (yearParam != null) {
            try {
                year = Integer.parseInt(yearParam);
            } catch (NumberFormatException e) {
                // Keep default year
            }
        }

        int[] monthCounts = new int[12]; // Jan=0 ... Dec=11

        String sql = "SELECT MONTH(start_time) AS month, COUNT(*) AS total " +
                     "FROM events WHERE YEAR(start_time) = ? GROUP BY MONTH(start_time)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");    // 1 to 12
                    int total = rs.getInt("total");
                    monthCounts[month - 1] = total;
                }
            }

            String json = new Gson().toJson(monthCounts);
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");  // return empty JSON array on error
        }
    }
}
