package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.DBConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet("/GenerateOPRServlet")
public class GenerateOPRServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int eventId = Integer.parseInt(request.getParameter("eventId"));

        // Event data
        String eventTitle = "", description = "";
        Timestamp startTime = null, endTime = null;
        String imgPath1 = null, imgPath2 = null, imgPath3 = null;

        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT e.title, e.start_time, e.end_time, eu.description, "
                    + "eu.file_path1, eu.file_path2, eu.file_path3 "
                    + "FROM events e LEFT JOIN event_uploads eu ON e.id = eu.event_id "
                    + "WHERE e.id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, eventId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    eventTitle = rs.getString("title");
                    startTime = rs.getTimestamp("start_time");
                    endTime = rs.getTimestamp("end_time");
                    description = rs.getString("description");
                    imgPath1 = rs.getString("file_path1");
                    imgPath2 = rs.getString("file_path2");
                    imgPath3 = rs.getString("file_path3");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Database error: " + e.getMessage());
            return;
        }

        // Setup PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=EventOPR_" + eventId + ".pdf");
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Header Logos Table
            PdfPTable logoTable = new PdfPTable(3);
            logoTable.setWidthPercentage(100);
            logoTable.setWidths(new float[]{1f, 1f, 1f});

            Image logo1 = safeLoadImage(getServletContext().getRealPath("/assets/images/SKKJ.png"));
            Image logo2 = safeLoadImage(getServletContext().getRealPath("/assets/images/kementerian (2).png"));

            PdfPCell leftLogoCell = new PdfPCell();
            if (logo1 != null) {
                logo1.scaleAbsolute(80f, 80f);
                leftLogoCell.addElement(logo1);
            }
            leftLogoCell.setBorder(Rectangle.NO_BORDER);
            leftLogoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell centerEmptyCell = new PdfPCell();
            centerEmptyCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell rightLogoCell = new PdfPCell();
            if (logo2 != null) {
                logo2.scaleAbsolute(80f, 80f);
                rightLogoCell.addElement(logo2);
            }
            rightLogoCell.setBorder(Rectangle.NO_BORDER);
            rightLogoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            logoTable.addCell(leftLogoCell);
            logoTable.addCell(centerEmptyCell);
            logoTable.addCell(rightLogoCell);

            document.add(logoTable);

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph((eventTitle != null ? eventTitle : "-"), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10);
            title.setSpacingAfter(10);
            document.add(title);

            // Time
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
            String timeString = (startTime != null && endTime != null)
                    ? sdf.format(startTime) + " hingga " + sdf.format(endTime)
                    : "-";
            document.add(new Paragraph("Masa Aktiviti: " + timeString));
            document.add(Chunk.NEWLINE);

            // Description
            document.add(new Paragraph("Deskripsi Aktiviti:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph(description != null ? description : "-"));
            document.add(Chunk.NEWLINE);

            // Activity Images
            // Activity Images (2x2 layout)
            document.add(new Paragraph("Gambar Aktiviti:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            Image img1 = safeLoadImage(getServletContext().getRealPath("/") + imgPath1);
            Image img2 = safeLoadImage(getServletContext().getRealPath("/") + imgPath2);
            Image img3 = safeLoadImage(getServletContext().getRealPath("/") + imgPath3);

            float imgW = 250f;
            float imgH = 250f;

// First row: 2 columns
            PdfPTable row1 = new PdfPTable(2);
            row1.setWidthPercentage(100);
            row1.setSpacingBefore(10f);
            row1.setWidths(new float[]{1f, 1f});
            row1.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            row1.addCell(getFixedImageCell(img1, imgW, imgH));
            row1.addCell(getFixedImageCell(img2, imgW, imgH));
            document.add(row1);

// Second row: 1 image centered in 2-column table
            if (img3 != null) {
                PdfPTable row2 = new PdfPTable(2);
                row2.setWidthPercentage(100);
                row2.setWidths(new float[]{1f, 1f});

                PdfPCell emptyCell = new PdfPCell(new Phrase(""));
                emptyCell.setBorder(Rectangle.NO_BORDER);

                PdfPCell imageCell = getFixedImageCell(img3, imgW, imgH);
                imageCell.setColspan(2); // span both columns
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                row2.addCell(imageCell); // just one cell spanning two columns
                document.add(row2);
            }
            
            // ✅ Finalize PDF
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "PDF generation error: " + e.getMessage());
        }
    }

    private Image safeLoadImage(String path) {
        try {
            if (path == null || path.trim().isEmpty()) {
                return null;
            }
            File file = new File(path);
            return file.exists() ? Image.getInstance(path) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private PdfPCell getFixedImageCell(Image img, float width, float height) {
    PdfPCell cell;
    if (img != null) {
        img.scaleToFit(width, height); // allows better fitting
        img.setAlignment(Image.ALIGN_CENTER);
        cell = new PdfPCell(img, true);
    } else {
        cell = new PdfPCell(new Phrase(""));
    }
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPadding(5);
    return cell;
}

}
