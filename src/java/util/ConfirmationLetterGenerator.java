package util;

import dao.EventDAO;
import dao.StudentDAO;
import model.Event;
import model.Student;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

public class ConfirmationLetterGenerator {

    public void generateLettersForEvent(int eventId) {
        try {
            EventDAO eventDAO = new EventDAO();
            StudentDAO studentDAO = new StudentDAO();

            Event event = eventDAO.getEventById(eventId);
            List<Student> participants = studentDAO.getStudentsByEvent(eventId);

            if (participants == null || participants.isEmpty()) {
                System.out.println("No participants found for this event.");
                return;
            }

            // Create uploads folder if not exists
            String uploadPath = new File(System.getProperty("catalina.base"), "webapps/SchoolActivityManagementSystem/uploads").getAbsolutePath();

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Use the first student's IC to name the PDF
            Student first = participants.get(0);
            String fileName = "surat_" + first.getIcNumber() + "_" + eventId + ".pdf";
            String fullFilePath = uploadPath + File.separator + fileName;

            // ✅ Generate PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fullFilePath));
            document.open();

            // Optional: school logo or styled header
            document.add(new Paragraph("SURAT PENGESAHAN AKTIVITI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("\nSenarai Peserta:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

            for (Student s : participants) {
                document.add(new Paragraph("Nama: " + s.getStudentName() + " | IC: " + s.getIcNumber() + " | Kelas: " + s.getStudentClass()));
            }

            document.add(new Paragraph("\nTajuk Aktiviti: " + event.getTitle()));
            document.add(new Paragraph("Tarikh & Masa: " + event.getStartTime() + " hingga " + event.getEndTime()));
            document.add(new Paragraph("\nDisahkan oleh pihak sekolah."));
            document.close();

          

            // ✅ Send email to each parent
            for (Student s : participants) {
                String parentEmail = studentDAO.getParentEmailByIc(s.getIcNumber());
                if (parentEmail != null && !parentEmail.trim().isEmpty()) {
                    String suratLink = "http://yourdomain.com/viewSurat.jsp?ic=" + s.getIcNumber() + "&event=" + eventId;
                    String msg = "Assalamualaikum / Salam Sejahtera,\n\n"
                               + "Surat pengesahan aktiviti untuk anak anda, " + s.getStudentName() + ", telah dijana.\n"
                               + "Sila klik pautan berikut untuk melihat surat: " + suratLink + "\n\n"
                               + "Sekian, terima kasih.";

                    MailUtil.sendMail("aqilah060404@gmail.com", "ucarcavfemzxvfmn",
                                      parentEmail, "Surat Pengesahan Aktiviti", msg);
                }
            }

            System.out.println("✅ Surat pengesahan dijana dan email dihantar kepada ibu bapa.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
