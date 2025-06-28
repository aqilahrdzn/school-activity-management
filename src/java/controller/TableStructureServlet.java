package controller;

import util.DBConfig;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/TableStructureServlet")
public class TableStructureServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] tables = {"booking", "classroom", "events", "event_participants", "event_uploads", "parent", "parent_approval", "student", "teachers"};
        Map<String, List<Map<String, String>>> tableData = new LinkedHashMap<>();

        try (Connection con = DBConfig.getConnection()) {
            System.out.println("✅ DB connected");

            for (String table : tables) {
                String sql = "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA " +
                             "FROM INFORMATION_SCHEMA.COLUMNS " +
                             "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, "school_management");  // ✅ Make sure this matches your database name
                    ps.setString(2, table);

                    try (ResultSet rs = ps.executeQuery()) {
                        List<Map<String, String>> columnList = new ArrayList<>();
                        while (rs.next()) {
                            Map<String, String> column = new LinkedHashMap<>();
                            column.put("Field", rs.getString("COLUMN_NAME"));
                            column.put("Type", rs.getString("COLUMN_TYPE"));
                            column.put("Null", rs.getString("IS_NULLABLE"));
                            column.put("Key", rs.getString("COLUMN_KEY"));
                            column.put("Default", rs.getString("COLUMN_DEFAULT"));
                            column.put("Extra", rs.getString("EXTRA"));
                            columnList.add(column);
                        }
                        tableData.put(table, columnList);
                    }
                }
            }

            request.setAttribute("tableData", tableData);
            request.getRequestDispatcher("/headmaster/reportViewer.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/headmaster/reportViewer.jsp").forward(request, response);
        }
    }
}
