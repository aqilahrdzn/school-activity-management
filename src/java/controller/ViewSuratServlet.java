/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import util.DBConfig;

import java.sql.SQLException;

@WebServlet("/ViewSuratServlet")
public class ViewSuratServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String eventIdStr = request.getParameter("eventId");

    if (eventIdStr == null || eventIdStr.trim().isEmpty() || eventIdStr.equals("null")) {
        response.setContentType("text/html");
        response.getWriter().println("<h3>Invalid or missing event ID.</h3>");
        return;
    }

    int eventId;
    try {
        eventId = Integer.parseInt(eventIdStr);
    } catch (NumberFormatException e) {
        response.setContentType("text/html");
        response.getWriter().println("<h3>Invalid event ID format.</h3>");
        return;
    }

    try (Connection con = DBConfig.getConnection()) {
        String sql = "SELECT surat_blob FROM events WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            InputStream input = rs.getBinaryStream("surat_blob");

            if (input != null) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=surat_pengesahan.pdf");

                OutputStream out = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                input.close();
                out.flush();
            } else {
                response.setContentType("text/html");
                response.getWriter().println("<h3>No surat available for this event.</h3>");
            }
        } else {
            response.setContentType("text/html");
            response.getWriter().println("<h3>Event not found.</h3>");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        response.setContentType("text/html");
        response.getWriter().println("<h3>Database error occurred.</h3>");
    }
}
}
