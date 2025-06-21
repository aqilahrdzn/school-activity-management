package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DBConfig;

public class LoginDAO {

    public String validateUser(String email, String password) {
        String teacherQuery = "SELECT role FROM teachers WHERE email = ? AND password = ?";
        String parentQuery = "SELECT 'parent' AS role FROM parent WHERE email = ? AND password = ?";

        try (Connection connection = DBConfig.getConnection()) {

            // Check teachers
            try (PreparedStatement teacherStmt = connection.prepareStatement(teacherQuery)) {
                teacherStmt.setString(1, email);
                teacherStmt.setString(2, password);
                ResultSet rs = teacherStmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    System.out.println("[LoginDAO] Teacher found with role: " + role);
                    return role;
                } else {
                    System.out.println("[LoginDAO] No teacher found with given credentials.");
                }
            }

            // Check parents
            try (PreparedStatement parentStmt = connection.prepareStatement(parentQuery)) {
                parentStmt.setString(1, email);
                parentStmt.setString(2, password);
                ResultSet rs = parentStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("[LoginDAO] Parent found.");
                    return rs.getString("role");  // should return 'parent'
                }
            }

        } catch (SQLException e) {
            System.out.println("[LoginDAO] Database error:");
            e.printStackTrace();
        }

        return null;
    }
}
