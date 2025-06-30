package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Notification;
import util.DBConfig;

public class NotificationDAO {

    public List<Notification> getNotificationsByUserIdAndRole(int userId, String role) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND user_role = ? ORDER BY created_at DESC";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification note = new Notification();
                note.setId(rs.getInt("id"));
                note.setMessage(rs.getString("message"));
                note.setCreatedAt(rs.getTimestamp("created_at"));
                note.setIsRead(rs.getInt("is_read")); // ✅ This line fixes the issue
                list.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

   public void insertNotifications(int userId, String role, String message, int eventId) {
    String sql = "INSERT INTO notifications (user_id, user_role, message, is_read, event_id) VALUES (?, ?, ?, 0, ?)";

    try (Connection conn = DBConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        stmt.setString(2, role);
        stmt.setString(3, message);
        stmt.setInt(4, eventId);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
   // For parent: insert notification without eventId
public void insertNotification(int userId, String role, String message) {
    String sql = "INSERT INTO notifications (user_id, user_role, message, is_read) VALUES (?, ?, ?, 0)";

    try (Connection conn = DBConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        stmt.setString(2, role);
        stmt.setString(3, message);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    public void markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Notification getNotificationById(int id) {
    Notification note = null;
    String sql = "SELECT * FROM notifications WHERE id = ?";

    try (Connection conn = DBConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            note = new Notification();
            note.setId(rs.getInt("id"));
            note.setMessage(rs.getString("message"));
            note.setCreatedAt(rs.getTimestamp("created_at"));
            note.setIsRead(rs.getInt("is_read"));
            note.setEventId(rs.getInt("event_id")); // 🟡 Make sure Notification model has getEventId()
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return note;
}

}
