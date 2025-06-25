/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

                String mimeType = getServletContext().getMimeType(fileName);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                response.setContentType(mimeType);
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

                OutputStream out = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                fileContent.close();
                out.flush();
            } else {
                response.getWriter().write("Resit file not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error loading resit file.");
        }
    }
}
