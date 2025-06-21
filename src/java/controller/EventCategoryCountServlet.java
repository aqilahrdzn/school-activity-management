/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.DBConfig;

/**
 *
 * @author Lenovo
 */
@WebServlet("/event-category-count")
public class EventCategoryCountServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT category, COUNT(*) AS count FROM events GROUP BY category";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<String> labels = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();

            while (rs.next()) {
                labels.add(rs.getString("category"));
                counts.add(rs.getInt("count"));
            }

            // Convert to JSON
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"labels\":").append(new Gson().toJson(labels)).append(",");
            json.append("\"counts\":").append(new Gson().toJson(counts));
            json.append("}");

            out.print(json.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Database error\"}");
        }
    }
}
