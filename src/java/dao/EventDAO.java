package dao;

import model.Event;
import util.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static util.DBConfig.getConnection;

public class EventDAO {

    /**
     * Inserts an event into the database.
     *
     * @param event The event to be stored.
     * @return True if the event is inserted successfully, false otherwise.
     */
    public boolean insertEvent(Event event) {
        boolean isInserted = false;
        String sql = "INSERT INTO events (category, title, description, start_time, end_time, time_zone, target_class, created_by, venue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set parameters for the SQL query
            ps.setString(1, event.getCategory());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setString(4, event.getStartTime());
            ps.setString(5, event.getEndTime());
            ps.setString(6, event.getTimeZone());
            ps.setString(7, event.getTargetClass());
            ps.setString(8, event.getCreatedBy());
            ps.setString(9, event.getVenue());  // Added venue

            // Execute the query
            int rows = ps.executeUpdate();
            if (rows > 0) {
                isInserted = true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting event into the database: " + e.getMessage());
            e.printStackTrace();
        }

        return isInserted;
    }

    public Event getEventById(int eventId) {
        Event event = null;
        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);  // Use setInt

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    event = new Event();
                    event.setCategory(rs.getString("category"));
                    event.setId(rs.getString("id"));  // If your Event id is String, keep this or convert to int
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setStartTime(rs.getString("start_time"));
                    event.setEndTime(rs.getString("end_time"));
                    event.setTimeZone(rs.getString("time_zone"));
                    event.setTargetClass(rs.getString("target_class"));
                    event.setVenue(rs.getString("venue"));  // Retrieve venue from DB
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving event from the database: " + e.getMessage());
            e.printStackTrace();
        }

        return event;
    }

    public boolean requestDeleteEvent(String eventId) {
        boolean isUpdated = false;
        String sql = "UPDATE events SET status = 'cancellation requested' WHERE id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                isUpdated = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating event status: " + e.getMessage());
            e.printStackTrace();
        }

        return isUpdated;
    }

    public List<Event> getEventsByCreator(String email) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE created_by = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event e = new Event();
                    e.setId(rs.getString("id"));
                    e.setCategory(rs.getString("category")); // ADD THIS
                    e.setTitle(rs.getString("title"));
                    e.setStartTime(rs.getString("start_time"));
                    e.setEndTime(rs.getString("end_time"));
                    e.setCreatedBy(rs.getString("created_by"));
                    events.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teacher events: " + e.getMessage());
            e.printStackTrace();
        }

        return events;
    }

    public void updateEventWithClassroom(int eventId, int classroomId) throws SQLException {
        String sql = "UPDATE events SET classroom_id = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classroomId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getAllEventDetails() {
        List<Map<String, Object>> events = new ArrayList<>();
        String sql = "SELECT e.id, e.title, e.category, e.description, e.start_time, e.end_time, "
                + "e.target_class, e.created_by, c.name AS classroom_name, e.venue "
                + "FROM events e LEFT JOIN classroom c ON e.classroom_id = c.id";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getString("id"));
                map.put("title", rs.getString("title"));
                map.put("category", rs.getString("category"));
                map.put("description", rs.getString("description"));
                map.put("start_time", rs.getString("start_time"));
                map.put("end_time", rs.getString("end_time"));
                map.put("target_class", rs.getString("target_class"));
                map.put("created_by", rs.getString("created_by"));
                map.put("classroom_name", rs.getString("classroom_name"));
                map.put("venue", rs.getString("venue"));  // Add venue to the event details
                events.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, category, title, description, start_time, end_time, created_by, payment_amount FROM events";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Event event = new Event();

                // ✅ Set ID to avoid "Invalid event ID"
                event.setId(String.valueOf(rs.getInt("id")));

                event.setCategory(rs.getString("category"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setStartTime(rs.getString("start_time"));
                event.setEndTime(rs.getString("end_time"));
                event.setCreatedBy(rs.getString("created_by"));

                // ✅ Set paymentAmount if exists
                event.setPaymentAmount(rs.getDouble("payment_amount"));

                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public int insertEventAndReturnId(Event event) {
        // MODIFICATION: Update the SQL query to include the new column
        String sql = "INSERT INTO events (category, title, description, start_time, end_time, time_zone, created_by, target_class, payment_amount, venue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int eventId = -1;

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getCategory());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setString(4, event.getStartTime());
            ps.setString(5, event.getEndTime());
            ps.setString(6, event.getTimeZone());
            ps.setString(7, event.getCreatedBy());
            ps.setString(8, event.getTargetClass());
            ps.setDouble(9, event.getPaymentAmount());
            ps.setString(10, event.getVenue());  // Set the venue field

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        eventId = rs.getInt(1); // Get the auto-generated event ID
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions properly
        }
        return eventId;
    }

    public void insertEventParticipant(int eventId, String studentIC) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO event_participants (event_id, student_ic) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setString(2, studentIC);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean deleteEventById(String eventId) {
        boolean isDeleted = false;
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Delete participants first
            String sql1 = "DELETE FROM event_participants WHERE event_id = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, Integer.parseInt(eventId));
            ps1.executeUpdate();

            // Step 2: Delete the event
            String sql2 = "DELETE FROM events WHERE id = ?";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, Integer.parseInt(eventId));
            int rows = ps2.executeUpdate();

            conn.commit(); // Commit transaction
            isDeleted = rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isDeleted;
    }

    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET category = ?, title = ?, description = ?, start_time = ?, end_time = ?, time_zone = ?, payment_amount = ?, venue = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getCategory());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getStartTime());
            stmt.setString(5, event.getEndTime());
            stmt.setString(6, event.getTimeZone());

            if (event.getPaymentAmount() > 0) {
                stmt.setDouble(7, event.getPaymentAmount());
            } else {
                stmt.setNull(7, java.sql.Types.DECIMAL);
            }

            stmt.setString(8, event.getVenue());  // Set the venue field
            stmt.setInt(9, Integer.parseInt(event.getId()));  // Assuming getId() returns a String

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGoogleEventId(int eventId, String googleEventId) {
        String sql = "UPDATE events SET google_event_id = ? WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, googleEventId);
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getGoogleEventIdByEventId(int eventId) {
        String sql = "SELECT google_event_id FROM events WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("google_event_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
