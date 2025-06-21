package dao;

import model.Classroom;
import util.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassroomDAO {

    public List<Classroom> getAvailableClassrooms(Timestamp startTime, Timestamp endTime) {
        List<Classroom> classrooms = new ArrayList<>();
        String sql = "SELECT * FROM classroom WHERE id NOT IN ("
                + "SELECT classroom_id FROM booking WHERE start_time < ? AND end_time > ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, endTime);
            stmt.setTimestamp(2, startTime);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Classroom c = new Classroom();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setCapacity(rs.getInt("capacity"));
                c.setStatus(rs.getString("status"));
                classrooms.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    public List<Classroom> getAvailableDuring(Timestamp start, Timestamp end) throws SQLException {
        List<Classroom> available = new ArrayList<>();

        String sql = "SELECT * FROM classroom c "
                + "WHERE c.id NOT IN ( "
                + "  SELECT b.classroom_id FROM booking b "
                + "  WHERE (? < b.end_time AND ? > b.start_time) "
                + ")";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, start);
            stmt.setTimestamp(2, end);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                available.add(new Classroom(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status")
                ));
            }
        }

        return available;
    }

    public void insertBooking(int classroomId, Timestamp start, Timestamp end) throws SQLException {
        String sql = "INSERT INTO booking (classroom_id, start_time, end_time) VALUES (?, ?, ?)";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classroomId);
            stmt.setTimestamp(2, start);
            stmt.setTimestamp(3, end);
            stmt.executeUpdate();
        }
    }

    // ✅ Rewritten without booked_until
    public void bookClassroom(int id, String status) throws SQLException {
        String sql = "UPDATE classroom SET status = ? WHERE id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public boolean isClassroomAvailable(int classroomId, Timestamp start, Timestamp end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE classroom_id = ? AND (? < end_time AND ? > start_time)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroomId);
            stmt.setTimestamp(2, start);
            stmt.setTimestamp(3, end);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }

    public List<Map<String, Object>> getAllBookings() throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT b.id, b.start_time, b.end_time, b.cancel_requested, b.cancel_approved, c.name AS classroom_name "
                + "FROM booking b JOIN classroom c ON b.classroom_id = c.id";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("start_time", rs.getTimestamp("start_time"));
                map.put("end_time", rs.getTimestamp("end_time"));
                map.put("cancel_requested", rs.getBoolean("cancel_requested"));
                map.put("cancel_approved", rs.getBoolean("cancel_approved"));
                map.put("classroom_name", rs.getString("classroom_name"));
                bookings.add(map);
            }
        }
        return bookings;
    }

    public void approveCancellation(int bookingId) throws SQLException {
        String sqlUpdateBooking = "UPDATE booking SET cancel_approved = TRUE WHERE id = ?";
        String sqlUpdateClassroom = "UPDATE classroom SET status = 'available' "
                + "WHERE id = (SELECT classroom_id FROM booking WHERE id = ?)";

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(sqlUpdateBooking); PreparedStatement stmt2 = conn.prepareStatement(sqlUpdateClassroom)) {

                stmt1.setInt(1, bookingId);
                stmt2.setInt(1, bookingId);

                stmt1.executeUpdate();
                stmt2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void requestCancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE booking SET cancel_requested = true WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }

    public void addClassroom(Classroom classroom) throws SQLException {
        String sql = "INSERT INTO classroom (name, capacity) VALUES (?, ?)";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, classroom.getName());
            stmt.setInt(2, classroom.getCapacity());
            stmt.executeUpdate();
        }
    }

    public void updateClassroomStatus(int id, String status) throws SQLException {
        String sql = "UPDATE classroom SET status = ? WHERE id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateClassroomStatuses() {
        String busySql = "UPDATE classroom SET status = 'booked' "
                + "WHERE id IN (SELECT classroom_id FROM booking WHERE ? BETWEEN start_time AND end_time)";
        String freeSql = "UPDATE classroom SET status = 'available' "
                + "WHERE id NOT IN (SELECT classroom_id FROM booking WHERE ? BETWEEN start_time AND end_time)";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement busyStmt = conn.prepareStatement(busySql); PreparedStatement freeStmt = conn.prepareStatement(freeSql)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            busyStmt.setTimestamp(1, now);
            freeStmt.setTimestamp(1, now);

            busyStmt.executeUpdate();
            freeStmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Classroom> getAllClassrooms() throws SQLException {
        List<Classroom> classrooms = new ArrayList<>();
        String sql = "SELECT * FROM classroom";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classrooms.add(new Classroom(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status")
                ));
            }
        }

        return classrooms;
    }

    public void deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM booking WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }

}
