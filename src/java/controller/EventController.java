package controller;

import dao.EventDAO;
import dao.StudentDAO; // Import StudentDAO
import dao.EventParticipantDAO; // Import EventParticipantDAO
import model.Event;
import model.Student;
import util.DBConfig; // Import DBConfig

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import java.util.*;

// --- NEW IMPORTS FOR EMAIL AND PDF (Ensure these are in your project) ---
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter; // For modern date/time formatting
import java.time.LocalDateTime; // For modern date/time objects
import java.time.ZoneId; // For time zone conversion
// --- END NEW IMPORTS ---

// --- NEW IMPORTS FOR DATABASE OPERATIONS ---
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
// --- END NEW IMPORTS ---

@WebServlet("/EventController")
public class EventController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("getStudentByIC".equals(action)) {
            String ic = request.getParameter("ic");
            // Assuming you have a StudentDAO with this method
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.getStudentByIC(ic);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            JSONObject jsonResponse = new JSONObject();

            if (student != null) {
                jsonResponse.put("success", true);
                jsonResponse.put("name", student.getStudentName());
                jsonResponse.put("ic", student.getIcNumber());
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Student with IC " + ic + " not found.");
            }
            out.print(jsonResponse.toString());
            out.flush();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("event-category");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String timeZone = request.getParameter("timeZone");
        // Add ":00" to match full date-time if user only entered HH:mm
        if (startTimeStr.length() == 16) {
            startTimeStr += ":00";
        }
        if (endTimeStr.length() == 16) {
            endTimeStr += ":00";
        }

// Validate that startTime is not in the past
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            if (startTime.isBefore(now)) {
                // Return an error response or redirect with error message
                response.getWriter().println("Error: Start time cannot be in the past.");
                return;
            }
        } catch (DateTimeParseException e) {
            response.getWriter().println("Error: Invalid date/time format.");
            return;
        }

        String createdBy = (String) request.getSession().getAttribute("email");
        String selectionType = request.getParameter("selectType");

        EventDAO eventDAO = new EventDAO();
        EventParticipantDAO participantDAO = new EventParticipantDAO(); // Initialize EventParticipantDAO
        StudentDAO studentDAO = new StudentDAO(); // Initialize StudentDAO

        try {
            // --- MODIFIED: Handle Payment Amount ---
            double paymentAmount = 0.0;
            if ("payment".equals(category)) {
                String amountStr = request.getParameter("paymentAmount");
                if (amountStr != null && !amountStr.trim().isEmpty()) {
                    try {
                        paymentAmount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        // Optional: Handle invalid input, e.g., redirect with an error
                        System.err.println("Invalid payment amount format: " + amountStr);
                        // For simplicity, we'll proceed with 0.0, but you could show an error page.
                    }
                }
            }
            // Create and store the event
            Event event = new Event();
            event.setCategory(category);
            event.setTitle(title);
            event.setDescription(description);
            event.setStartTime(startTimeStr);
            event.setEndTime(endTimeStr);
            event.setTimeZone(timeZone);
            event.setCreatedBy(createdBy);

            event.setPaymentAmount(paymentAmount);

            String[] selectedClasses = request.getParameterValues("classDropdown");
            String[] selectedICsFromIndividual = request.getParameterValues("selectedICs"); // Get individual ICs from hidden inputs
            String sportTeam = request.getParameter("sportDropdown");
            String uniformUnit = request.getParameter("uniformDropdown");

            // Set target class for event record (can be empty string if not by class)
            event.setTargetClass(String.join(", ", selectedClasses != null ? selectedClasses : new String[]{}));

            int eventId = eventDAO.insertEventAndReturnId(event);
            if (eventId == -1) {
                response.getWriter().println("Failed to store the event in the database.");
                // Set session attribute
                return;
            }

            // A Set to store unique ICs for all participants involved in this event
            // This set will be used to gather student details for email/PDF generation
            Set<String> participantICsForEmailAndPDF = new HashSet<>();

            // Add participants to the database using the appropriate EventParticipantDAO method
            // And collect their ICs for later email/PDF generation
            switch (selectionType) {
                case "class":
                    if (selectedClasses != null && selectedClasses.length > 0) {
                        List<String> classesList = Arrays.asList(selectedClasses);
                        participantDAO.addParticipantsByClass(classesList, eventId); // Use your existing method
                        // After adding by class, retrieve all relevant student ICs for emailing
                        for (String className : classesList) {
                            List<String> ics = new ArrayList<>();
                            // Need to query StudentDAO to get ICs for this class
                            String sql = "SELECT ic_number FROM student WHERE class = ?";
                            try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1, className);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    while (rs.next()) {
                                        ics.add(rs.getString("ic_number"));
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace(); // Log the SQL exception
                            }
                            participantICsForEmailAndPDF.addAll(ics);
                        }
                    }
                    break;
                case "individual":
                    if (selectedICsFromIndividual != null) {
                        for (String ic : selectedICsFromIndividual) {
                            if (ic != null && !ic.trim().isEmpty()) {
                                participantDAO.addParticipantByIC(ic.trim(), eventId); // Use your existing method
                                participantICsForEmailAndPDF.add(ic.trim()); // Add to our set for email/PDF
                            }
                        }
                    }
                    break;
                case "sport":
                    if (sportTeam != null && !sportTeam.isEmpty()) {
                        participantDAO.addParticipantsBySport(sportTeam, eventId); // Use your existing method
                        // After adding by sport, retrieve all relevant student ICs for emailing
                        String sql = "SELECT ic_number FROM student WHERE sport_team = ?";
                        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, sportTeam);
                            try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    participantICsForEmailAndPDF.add(rs.getString("ic_number"));
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace(); // Log the SQL exception
                        }
                    }
                    break;
                case "uniform":
                    if (uniformUnit != null && !uniformUnit.isEmpty()) {
                        participantDAO.addParticipantsByUniform(uniformUnit, eventId); // Use your existing method
                        // After adding by uniform, retrieve all relevant student ICs for emailing
                        String sql = "SELECT ic_number FROM student WHERE uniform_unit = ?";
                        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, uniformUnit);
                            try (ResultSet rs = stmt.executeQuery()) {
                                while (rs.next()) {
                                    participantICsForEmailAndPDF.add(rs.getString("ic_number"));
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace(); // Log the SQL exception
                        }
                    }
                    break;

            }

            // Ensure the seconds part is included (Google Calendar requires HH:mm:ss)
            if (startTimeStr.length() == 16) {
                startTimeStr += ":00"; // convert '2025-06-23T14:30' to '2025-06-23T14:30:00'
            }
            if (endTimeStr.length() == 16) {
                endTimeStr += ":00";
            }
            // Send event to Google Calendar (your existing code)
            JSONObject eventJson = new JSONObject();
            eventJson.put("summary", title);
            eventJson.put("description", description);

            JSONObject start = new JSONObject();
            start.put("dateTime", startTimeStr);
            start.put("timeZone", timeZone);
            eventJson.put("start", start);

            JSONObject end = new JSONObject();
            end.put("dateTime", endTimeStr);
            end.put("timeZone", timeZone);
            eventJson.put("end", end);

            // You might want to move this API key to a configuration file or environment variable
            URL url = new URL("https://v1.nocodeapi.com/aqilah/calendar/mtudlycLrYWeEomM/event");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(eventJson.toString().getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // ✅ Now safe to redirect or do logic like bookingClass.jsp
                if ("school".equals(category)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("eventId", eventId);
                    session.setAttribute("category", category);
                    session.setAttribute("eventTitle", title);
                    session.setAttribute("description", description);
                    session.setAttribute("eventStartTime", startTimeStr);
                    session.setAttribute("eventEndTime", endTimeStr);
                    session.setAttribute("timeZone", timeZone);
                    response.sendRedirect(request.getContextPath() + "/teacher/bookingClass.jsp?success=true&category=" + category);
                    return;
                }

                String fullPdfPath = null; // To store the path of the generated PDF
                String pdfFileName = null; // To store the filename of the generated PDF
                // --- NEW: Email and PDF Generation Logic ---
                // This loop now processes the collected unique participant ICs
                for (String studentIC : participantICsForEmailAndPDF) {
                    Student student = studentDAO.getStudentByIC(studentIC); // Get full student details, including parent's email
                    if (student != null && student.getParentEmail() != null && !student.getParentEmail().isEmpty()) {
                        String parentEmail = student.getParentEmail();
                        String studentName = student.getStudentName();

                        // 1. Generate Confirmation Letter (PDF)
                        // IMPORTANT: Ensure this path is writable by your web server
                        // This uses a context path, which is safer for web applications
                        String filePath = getServletContext().getRealPath("/WEB-INF/confirmation_letters") + File.separator;
                        File uploadDir = new File(filePath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs(); // Create directory if it doesn't exist
                        }
                        // Sanitize filename to prevent issues
                        pdfFileName = "Surat_Pengesahan_" + studentName.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + eventId + ".pdf";
                        fullPdfPath = filePath + pdfFileName;

                        try {
                            generateConfirmationLetter(fullPdfPath, title, description, startTimeStr, endTimeStr, studentName);
                            System.out.println("Generated PDF for " + studentName + " at: " + fullPdfPath);

                            // 2. Send Email with PDF Attachment
                            sendEmailWithAttachment(parentEmail, studentName, title, description, fullPdfPath, pdfFileName);
                            System.out.println("Sent email to " + parentEmail + " for student " + studentName);

                        } catch (Exception emailPdfEx) {
                            System.err.println("Error generating PDF or sending email for student " + studentName + ": " + emailPdfEx.getMessage());
                            emailPdfEx.printStackTrace();
                            // Optionally, log this error to a file or database for later review
                        }
                    } else {
                        System.out.println("Skipping email/PDF for student IC " + studentIC + ": Student not found or parent email missing.");
                    }
                }
                // --- END NEW: Email and PDF Generation Logic ---

                // --- NEW: Save PDF to DB ---
                if (fullPdfPath != null && pdfFileName != null) {
                    try (Connection dbConnection = DBConfig.getConnection(); InputStream fileInputStream = new FileInputStream(fullPdfPath)) {
                        String updateSQL = "UPDATE events SET surat_pengesahan=?, surat_blob=? WHERE id=?";
                        try (PreparedStatement ps = dbConnection.prepareStatement(updateSQL)) {
                            ps.setString(1, "confirmation_letters/" + pdfFileName); // Store a relative path if desired
                            ps.setBlob(2, fileInputStream);
                            ps.setInt(3, eventId);
                            ps.executeUpdate();
                            System.out.println("PDF saved to database for event ID: " + eventId);
                        }
                    } catch (SQLException e) {
                        System.err.println("Error saving PDF to database: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                // --- END NEW: Save PDF to DB ---

                request.setAttribute("success", true);
                request.setAttribute("category", category);
                HttpSession session = request.getSession();
                session.setAttribute("eventId", eventId);
                session.setAttribute("eventTitle", title);
                session.setAttribute("eventStartTime", startTimeStr);
                session.setAttribute("eventEndTime", endTimeStr);
                // Redirect back to the JSP, indicating success.
                response.sendRedirect(request.getContextPath() + "/teacher/eventList.jsp?success=true&category=" + category);
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                // Send a response indicating partial success (event saved, but calendar failed)
                response.getWriter().println("Event saved, but failed to add to Google Calendar. Error: " + errorResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // --- NEW HELPER METHODS BELOW ---
    /**
     * Sends an email with a PDF attachment to the specified recipient.
     *
     * @param recipientEmail The email address of the parent.
     * @param studentName The name of the student.
     * @param eventTitle The title of the event.
     * @param eventDescription The description of the event.
     * @param attachmentPath The full file path to the PDF attachment.
     * @param attachmentFileName The name of the file to appear in the email.
     * @throws MessagingException If there's an error sending the email.
     */
    private void sendEmailWithAttachment(String recipientEmail, String studentName, String eventTitle, String eventDescription, String attachmentPath, String attachmentFileName) throws MessagingException {
        // --- IMPORTANT: CONFIGURE YOUR EMAIL SETTINGS HERE ---
        final String senderEmail = "aqilah060404@gmail.com"; // <-- REPLACE WITH YOUR GMAIL ADDRESS
        final String senderPassword = "nbsofzvrdriryeaj"; // <-- REPLACE WITH YOUR GMAIL APP PASSWORD
        // For Gmail: You need to enable 2-Step Verification and generate an App Password.
        // Go to myaccount.google.com -> Security -> 2-Step Verification -> App passwords.
        // DO NOT use your regular Gmail password here.
        // --- END IMPORTANT CONFIG ---

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Kebenaran Penyertaan Acara: " + eventTitle); // Malay subject

        // Create the message body part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Assalamualaikum dan Salam Sejahtera,\n\n"
                + "Dengan hormatnya dimaklumkan bahawa anak/jagaan tuan/puan, **" + studentName + "**, telah disahkan untuk menyertai acara berikut:\n\n"
                + "**Tajuk Acara:** " + eventTitle + "\n"
                + "**Keterangan:** " + eventDescription + "\n\n"
                + "Sila rujuk lampiran 'Surat Kebenaran' untuk butiran lanjut mengenai penyertaan anak/jagaan tuan/puan.\n\n"
                + "Terima kasih atas perhatian dan kerjasama tuan/puan.\n\n"
                + "Yang Benar,\n"
                + "Pihak Pengurusan Sekolah");

        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is the attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachmentPath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(attachmentFileName); // This is the name shown in the email
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Generates a "Surat Pengesahan" (Confirmation Letter) in PDF format.
     *
     * @param filePath The full path where the PDF should be saved.
     * @param eventTitle The title of the event.
     * @param eventDescription The description of the event.
     * @param startTimeStr The start time of the event (YYYY-MM-DDTHH:mm:ss).
     * @param endTimeStr The end time of the event (YYYY-MM-DDTHH:mm:ss).
     * @param studentName The name of the student.
     * @throws DocumentException If there's an error creating the PDF document.
     * @throws IOException If there's an I/O error (e.g., file writing).
     */
    private void generateConfirmationLetter(String filePath, String eventTitle, String eventDescription,
            String startTimeStr, String endTimeStr, String studentName)
            throws DocumentException, IOException { // Removed ParseException, using LocalDateTime

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Fonts for the document
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Font signatureFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

        // Header/School Info (Optional: Add your school's name/address here)
        document.add(new Paragraph("SEKOLAH KEBANGSAAN KERAYONG JAYA", headerFont));
        document.add(new Paragraph("SK KERAYONG JAYA, 28200 BANDAR BERA, PAHANG", bodyFont));
        document.add(new Paragraph("Telefon: 09-2507086 | Faks: 09-2507087", bodyFont));
        document.add(new Paragraph("Emel: cbaa152.skkj@gmail.com", bodyFont));
        document.add(new Paragraph("_____________________________________________________________________________\n\n", bodyFont));

        // Title of the letter
        Paragraph titlePara = new Paragraph("SURAT KEBENARAN PENYERTAAN ACARA\n\n", titleFont);
        titlePara.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titlePara);

        // Date of Letter
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ms", "MY")); // Use Malay locale for months
        Paragraph dateParagraph = new Paragraph("Tarikh: " + todayDateFormat.format(new Date()) + "\n\n", bodyFont);
        dateParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(dateParagraph);

        // Recipient Salutation
        Paragraph salutation = new Paragraph("Kepada Penjaga " + studentName + ",\n\n", bodyFont);
        document.add(salutation);

        // Reference (Optional)
        document.add(new Paragraph("Perkara: Kebenaran Penyertaan Acara **" + eventTitle + "**\n\n", headerFont));

        // Body content
        Paragraph body1 = new Paragraph(
                "Dengan segala hormatnya, kami ingin memaklumkan bahawa anak/jagaan tuan/puan,\n\n", bodyFont);
        document.add(body1);

        Paragraph studentInfo = new Paragraph(
                "Nama: " + studentName + "\n\n", signatureFont); // Highlight student name
        studentInfo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(studentInfo);

        Paragraph body2 = new Paragraph(
                "telah disahkan sebagai peserta dalam acara berikut:", bodyFont);
        document.add(body2);

        // Event Details
        document.add(new Paragraph("\n")); // Spacer
        Paragraph eventDetailsHeader = new Paragraph("BUTIRAN ACARA:\n", headerFont);
        eventDetailsHeader.setIndentationLeft(30); // Indent for better readability
        document.add(eventDetailsHeader);

        // Date and time formatting for event times
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ms", "MY"));
        DateTimeFormatter outputTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String formattedStartDate = "";
        String formattedStartTime = "";
        String formattedEndDate = "";
        String formattedEndTime = "";

        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startTimeStr, inputFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endTimeStr, inputFormatter);

            formattedStartDate = startDateTime.format(outputDateFormatter);
            formattedStartTime = startDateTime.format(outputTimeFormatter);
            formattedEndDate = endDateTime.format(outputDateFormatter);
            formattedEndTime = endDateTime.format(outputTimeFormatter);
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing date/time strings for PDF: " + e.getMessage());
            // Fallback or handle error
            formattedStartDate = "N/A";
            formattedStartTime = "N/A";
            formattedEndDate = "N/A";
            formattedEndTime = "N/A";
        }

        document.add(new Paragraph("Tajuk Acara: " + eventTitle, bodyFont));
        document.add(new Paragraph("Keterangan: " + eventDescription, bodyFont));
        document.add(new Paragraph("Tarikh Mula: " + formattedStartDate, bodyFont));
        document.add(new Paragraph("Masa Mula: " + formattedStartTime, bodyFont));
        document.add(new Paragraph("Tarikh Tamat: " + formattedEndDate, bodyFont));
        document.add(new Paragraph("Masa Tamat: " + formattedEndTime + "\n\n", bodyFont));

        Paragraph body3 = new Paragraph(
                "Kebenaran anak/jagaan tuan/puan adalah penting dan diharapkan dapat memberikan manfaat kepada semua pihak.\n\n"
                + "Sekiranya terdapat sebarang pertanyaan atau memerlukan maklumat lanjut, sila hubungi pihak sekolah di talian 09-2507086.\n\n"
                + "Kerjasama dan perhatian tuan/puan dalam perkara ini amat kami hargai.\n\n"
                + "Sekian, terima kasih.\n\n", bodyFont);
        document.add(body3);

        // Closing
        document.add(new Paragraph("\"BERKHIDMAT UNTUK NEGARA\"\n\n", signatureFont));

        // Signature
        document.add(new Paragraph("Saya yang menjalankan amanah,\n\n\n\n", bodyFont)); // Space for signature
        document.add(new Paragraph("_____________________________\n", bodyFont));
        document.add(new Paragraph("PN MAZITA BINTI ROSLI", signatureFont));
        document.add(new Paragraph("GURU BESAR ", bodyFont));
        document.add(new Paragraph("SEKOLAH KEBANGSAAN KERAYONG JAYA", bodyFont));

        document.close();
    }
}
