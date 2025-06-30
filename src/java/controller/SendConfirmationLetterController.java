package controller;

import dao.EventDAO;
import dao.StudentDAO;
import dao.EventParticipantDAO;
import model.Event;
import model.Student;
import util.DBConfig;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.NotificationDAO;
import dao.ParentDAO;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import javax.mail.util.ByteArrayDataSource;
import model.Parent;

@WebServlet("/SendConfirmationLetterController")
public class SendConfirmationLetterController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing event ID.");
            return;
        }

        int eventId = Integer.parseInt(eventIdParam);
        EventDAO eventDAO = new EventDAO();
        StudentDAO studentDAO = new StudentDAO();
        EventParticipantDAO participantDAO = new EventParticipantDAO();
        ParentDAO parentDAO = new ParentDAO();
        NotificationDAO notificationDAO = new NotificationDAO();

        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found.");
            return;
        }

        List<String> studentICs = participantDAO.getICsByEventId(eventId);

        String filePath = getServletContext().getRealPath("/WEB-INF/confirmation_letters") + File.separator;
        File uploadDir = new File(filePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String pdfFileName = "Surat_Pengesahan_Event_" + eventId + ".pdf";

        try {
            generateConfirmationLetter(baos, event, studentICs, studentDAO);
            byte[] pdfBytes = baos.toByteArray();

            // Save surat_blob and surat_pengesahan to events table
            try (Connection conn = DBConfig.getConnection()) {
                String sql = "UPDATE events SET surat_blob = ?, surat_pengesahan = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setBytes(1, pdfBytes);
                ps.setString(2, pdfFileName);
                ps.setInt(3, eventId);
                ps.executeUpdate();
            }

            // Email and notify each parent
            for (String studentIC : studentICs) {
                Student student = studentDAO.getStudentByIC(studentIC);
                if (student != null && student.getParentEmail() != null && !student.getParentEmail().isEmpty()) {
                    sendEmailWithAttachment(student.getParentEmail(), student.getStudentName(),
                            event.getTitle(), event.getDescription(), pdfBytes, pdfFileName);

                    Parent parent = parentDAO.getParentByStudentId(student.getId()); // fixed method
                    if (parent != null) {
                        notificationDAO.insertNotification(
                            parent.getId(),
                            "parent",
                            "Surat kebenaran telah diterima. Sila semak surat lampiran."
                        );
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/teacher/eventList.jsp?status=lettersent");
    }

    private void generateConfirmationLetter(OutputStream outputStream, Event event, List<String> studentICs, StudentDAO studentDAO) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font signatureFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

        // === Create header with school info + logo side by side ===
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{3f, 1f});

        // Left side: School Info
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.addElement(new Paragraph("SEKOLAH KEBANGSAAN KERAYONG JAYA", headerFont));
        textCell.addElement(new Paragraph("SK KERAYONG JAYA, 28200 BANDAR BERA, PAHANG", bodyFont));
        textCell.addElement(new Paragraph("Telefon: 09-2507086 | Faks: 09-2507087", bodyFont));
        textCell.addElement(new Paragraph("Emel: cbaa152.skkj@gmail.com", bodyFont));
        headerTable.addCell(textCell);

        // Right side: Logo
        try {
            String logoPath = getServletContext().getRealPath("/assets/images/SKKJ.png");
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(90, 90);
            PdfPCell logoCell = new PdfPCell(logo, false);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            System.err.println("Logo error: " + e.getMessage());
            headerTable.addCell(""); // Fallback empty cell
        }

        document.add(headerTable);
        document.add(new Paragraph("_____________________________________________________________________________\n\n", bodyFont));

        Paragraph titlePara = new Paragraph("SURAT KEBENARAN PENYERTAAN ACARA\n\n", titleFont);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ms", "MY"));
        Paragraph dateParagraph = new Paragraph("Tarikh: " + todayDateFormat.format(new Date()) + "\n\n", bodyFont);
        dateParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(dateParagraph);

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ms", "MY"));
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

        String startDate = "N/A", startTime = "N/A", endDate = "N/A", endTime = "N/A";
        try {
            LocalDateTime start = LocalDateTime.parse(event.getStartTime(), inputFormat);
            LocalDateTime end = LocalDateTime.parse(event.getEndTime(), inputFormat);
            startDate = start.format(dateFormat);
            startTime = start.format(timeFormat);
            endDate = end.format(dateFormat);
            endTime = end.format(timeFormat);
        } catch (Exception e) {
            System.err.println("Date format error: " + e.getMessage());
        }

        for (String ic : studentICs) {
            Student student = studentDAO.getStudentByIC(ic);
            if (student == null) {
                continue;
            }

            document.add(new Paragraph("Kepada Penjaga " + student.getStudentName() + ",\n\n", bodyFont));
            document.add(new Paragraph("Perkara: Kebenaran Penyertaan Acara \"" + event.getTitle() + "\"\n\n", headerFont));
            document.add(new Paragraph("Dengan segala hormatnya, kami ingin memaklumkan bahawa anak/jagaan tuan/puan,\n", bodyFont));
            Paragraph studentInfo = new Paragraph("Nama: " + student.getStudentName() + "\n", signatureFont);
            studentInfo.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(studentInfo);
            document.add(new Paragraph("telah disahkan sebagai peserta dalam acara berikut:\n", bodyFont));

            document.add(new Paragraph("\nBUTIRAN ACARA:", headerFont));
            document.add(new Paragraph("Tajuk Acara: " + event.getTitle(), bodyFont));
            document.add(new Paragraph("Keterangan: " + event.getDescription(), bodyFont));
            document.add(new Paragraph("Tarikh Mula: " + startDate, bodyFont));
            document.add(new Paragraph("Masa Mula: " + startTime, bodyFont));
            document.add(new Paragraph("Tarikh Tamat: " + endDate, bodyFont));
            document.add(new Paragraph("Masa Tamat: " + endTime + "\n\n", bodyFont));

            document.add(new Paragraph("Kebenaran anak/jagaan tuan/puan adalah penting dan diharapkan dapat memberikan manfaat kepada semua pihak.\n\n"
                    + "Sekiranya terdapat sebarang pertanyaan atau memerlukan maklumat lanjut, sila hubungi pihak sekolah di talian 09-2507086.\n\n"
                    + "Kerjasama dan perhatian tuan/puan dalam perkara ini amat kami hargai.\n\n"
                    + "Sekian, terima kasih.\n\n", bodyFont));

            document.add(new Paragraph("\"BERKHIDMAT UNTUK NEGARA\"\n\n", signatureFont));
            document.add(new Paragraph("Saya yang menjalankan amanah,\n\n\n\n", bodyFont));
            document.add(new Paragraph("_____________________________\n", bodyFont));
            document.add(new Paragraph("PN MAZITA BINTI ROSLI", signatureFont));
            document.add(new Paragraph("GURU BESAR", bodyFont));
            document.add(new Paragraph("SEKOLAH KEBANGSAAN KERAYONG JAYA", bodyFont));
            document.newPage();
        }

        document.close();
    }

    private void sendEmailWithAttachment(String recipientEmail, String studentName, String eventTitle, String eventDescription, byte[] pdfBytes, String attachmentFileName) throws MessagingException {
        final String senderEmail = "aqilah060404@gmail.com";
        final String senderPassword = "nbsofzvrdriryeaj";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Kebenaran Penyertaan Acara: " + eventTitle);

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Assalamualaikum dan Salam Sejahtera,\n\n"
                + "Anak/jagaan tuan/puan, " + studentName + ", telah disahkan untuk menyertai acara: " + eventTitle + ".\n"
                + "Sila rujuk lampiran surat untuk maklumat lanjut.\n\nTerima kasih.");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachmentFileName);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
        Transport.send(message);
    }
}
