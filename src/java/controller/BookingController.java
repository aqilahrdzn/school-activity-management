package controller;

import dao.ClassroomDAO;
import dao.EventDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/classroom")
public class BookingController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            ClassroomDAO dao = new ClassroomDAO();

            if ("delete".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                dao.deleteBooking(bookingId);
                response.sendRedirect("my_booking.jsp?message=Booking+deleted+successfully");
                return;
            }

            String eventId = request.getParameter("eventId");
            if (eventId == null || eventId.trim().isEmpty()) {
                response.sendRedirect("booking.jsp?error=Invalid+event+ID.");
                return;
            }

            if ("cancel".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                dao.requestCancelBooking(bookingId);
                response.sendRedirect("my_bookings.jsp?message=Request+sent+to+staff");
                return;
            }

            int classroomId = Integer.parseInt(request.getParameter("classroomId"));
            String startTimeStr = request.getParameter("startTime");
            int duration = Integer.parseInt(request.getParameter("duration"));

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp startTime = new Timestamp(sdf.parse(startTimeStr).getTime());
            Timestamp endTime = new Timestamp(startTime.getTime() + duration * 60 * 1000);

            boolean isAvailable = dao.isClassroomAvailable(classroomId, startTime, endTime);
            if (!isAvailable) {
                response.sendRedirect("booking.jsp?error=This+classroom+is+already+booked+at+that+time.");
                return;
            }

            dao.insertBooking(classroomId, startTime, endTime);
            dao.bookClassroom(classroomId, "booked");

            EventDAO eventDAO = new EventDAO();
            eventDAO.updateEventWithClassroom(Integer.parseInt(eventId), classroomId);

            response.sendRedirect(request.getContextPath() + "/teacher/EventList.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Booking failed", e);
        }
    }
}
