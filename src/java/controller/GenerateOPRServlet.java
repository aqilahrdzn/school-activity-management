/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import util.DBConfig;

@WebServlet("/GenerateOPRServlet")
public class GenerateOPRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int eventId = Integer.parseInt(request.getParameter("eventId"));
        String eventTitle = "";
        String startTime = "";
        String endTime = "";
        String description = "";
        String imagePath = "";

        try (Connection conn = DBConfig.getConnection()) {
            // Get event details
            String sql1 = "SELECT title, start_time, end_time FROM events WHERE id = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                stmt1.setInt(1, eventId);
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next()) {
                    eventTitle = rs1.getString("title");
                    startTime = rs1.getString("start_time");
                    endTime = rs1.getString("end_time");
                }
            }

            // Get latest description and image path from uploads
            String sql2 = "SELECT file_path FROM event_uploads WHERE event_id = ? ORDER BY uploaded_at DESC LIMIT 1";
            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                stmt2.setInt(1, eventId);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    imagePath = getServletContext().getRealPath("") + File.separator + rs2.getString("file_path");
                }
            }

            // Get description from textarea if needed
            description = request.getParameter("description");

        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage());
        }

        // PDF output settings
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=OPR_" + eventTitle.replaceAll(" ", "_") + ".pdf");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("One Page Report: " + eventTitle, titleFont));
            document.add(Chunk.NEWLINE);

            // Date and Time
            document.add(new Paragraph("Date & Time: " + startTime + " - " + endTime));
            document.add(Chunk.NEWLINE);

            // Description
            document.add(new Paragraph("Description:"));
            document.add(new Paragraph(description));
            document.add(Chunk.NEWLINE);

            // Image (if exists)
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image image = Image.getInstance(imagePath);
                    image.scaleToFit(400, 300);
                    document.add(image);
                } catch (Exception e) {
                    document.add(new Paragraph("Image could not be loaded."));
                }
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("PDF creation failed: " + e.getMessage());
        }
    }
}
