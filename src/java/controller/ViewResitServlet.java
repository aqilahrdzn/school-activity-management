/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.DBConfig;

/**
 *
 * @author Lenovo
 */
@WebServlet("/ViewResitServlet")
public class ViewResitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventId = request.getParameter("event_id");
        String parentId = request.getParameter("parent_id");

        try (Connection con = DBConfig.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT resit_blob, resit_file FROM parent_approval WHERE event_id = ? AND parent_id = ?"
            );
            ps.setInt(1, Integer.parseInt(eventId));
            ps.setInt(2, Integer.parseInt(parentId));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                InputStream fileContent = rs.getBinaryStream("resit_blob");
                String fileName = rs.getString("resit_file");

                if (fileContent != null) {
                    // Try to detect MIME type from file name or fallback
                    String mimeType = getServletContext().getMimeType(fileName);
                    if (mimeType == null) {
                        mimeType = "application/octet-stream";
                    }

                    response.setContentType(mimeType);
                    response.setHeader("Content-Disposition", "inline; filename=\"" + (fileName != null ? fileName : "resit.pdf") + "\"");

                    OutputStream out = response.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = fileContent.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    fileContent.close();
                    out.flush();
                } else {
                    // BLOB is null
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<h3>Resit BLOB not found in database.</h3>");
                }
            } else {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<h3>Resit not found for this event and parent.</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h3>Error loading resit file: " + e.getMessage() + "</h3>");
        }
    }
}
