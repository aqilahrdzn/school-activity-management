package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.DBConfig;

public class EventParticipantDAO {

    public boolean addParticipantsByClass(List<String> classes, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, student_ic) " +
                     "SELECT ?, ic_number FROM student WHERE class = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String cls : classes) {
                stmt.setInt(1, eventId);
                stmt.setString(2, cls);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addParticipantByIC(String icNumber, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, student_ic) VALUES (?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, icNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean addParticipantsBySport(String sportTeam, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, student_ic) " +
                     "SELECT ?, ic_number FROM student WHERE sport_team = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, sportTeam);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addParticipantsByUniform(String uniformUnit, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, student_ic) " +
                     "SELECT ?, ic_number FROM student WHERE uniform_unit = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, uniformUnit);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getICsByEventId(int eventId) {
        List<String> studentICs = new ArrayList<>();
        String sql = "SELECT student_ic FROM event_participants WHERE event_id = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    studentICs.add(rs.getString("student_ic"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentICs;
    }
}
