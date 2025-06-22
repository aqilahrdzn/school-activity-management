package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Teacher;
import util.DBConfig;

public class TeacherDAO {

    // Get teacher details by email (including profile picture)
    public Teacher getTeacherDetails(String email) {
    String query = "SELECT * FROM teachers WHERE email = ?";
    try (Connection connection = DBConfig.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Teacher teacher = new Teacher();
            teacher.setId(resultSet.getInt("id"));
            teacher.setName(resultSet.getString("name"));
            teacher.setEmail(resultSet.getString("email"));
            teacher.setContactNumber(resultSet.getString("contact_number"));
            teacher.setRole(resultSet.getString("role"));
            teacher.setProfilePicture(resultSet.getString("profile_picture"));
            teacher.setIsGuruKelas(resultSet.getString("is_guru_kelas"));
            teacher.setKelas(resultSet.getString("kelas"));
            teacher.setPassword(resultSet.getString("password")); // ✅ IMPORTANT: add this
            return teacher;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}


    // Insert a new teacher (including optional profile picture)
    public boolean insertTeacher(Teacher teacher) {
        String query = "INSERT INTO teachers (name, email, password, contact_number, ic_number, role, profile_picture, is_guru_kelas, kelas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getEmail());
            preparedStatement.setString(3, teacher.getPassword());
            preparedStatement.setString(4, teacher.getContactNumber());
            preparedStatement.setString(5, teacher.getIcNumber());
            preparedStatement.setString(6, teacher.getRole());
            preparedStatement.setString(7, teacher.getProfilePicture());
            preparedStatement.setString(8, teacher.getIsGuruKelas());
            preparedStatement.setString(9, teacher.getKelas());

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
     public Teacher authenticateTeacher(String email, String password) {
        String sql = "SELECT id, name, email, password, contact_number, ic_number, role, is_guru_kelas, kelas FROM teacher WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // In a real app, compare hashed passwords: BCrypt.checkpw(password, rs.getString("password"))
                if (password.equals(rs.getString("password"))) { // DANGER: Plain password check
                    Teacher teacher = new Teacher();
                    teacher.setId(rs.getInt("id"));
                    teacher.setName(rs.getString("name"));
                    teacher.setEmail(rs.getString("email"));
                    teacher.setPassword(rs.getString("password")); // Hashed password
                    teacher.setContactNumber(rs.getString("contact_number"));
                    teacher.setIcNumber(rs.getString("ic_number"));
                    teacher.setRole(rs.getString("role"));
                    teacher.setIsGuruKelas(rs.getString("is_guru_kelas"));
                    teacher.setKelas(rs.getString("kelas"));
                    return teacher;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Optional: Update name, email, and profile picture
    public boolean updateTeacherAccount(Teacher teacher) {
        String query = "UPDATE teachers SET name = ?, email = ?, profile_picture = ? WHERE id = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, teacher.getName());
            preparedStatement.setString(2, teacher.getEmail());
            preparedStatement.setString(3, teacher.getProfilePicture());
            preparedStatement.setInt(4, teacher.getId());

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isClassAssignedToGuruKelas(String kelas) {
    boolean isAssigned = false;
    String query = "SELECT COUNT(*) FROM teachers WHERE is_guru_kelas = 'Yes' AND kelas = ?";
    try (Connection conn = DBConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, kelas);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                isAssigned = rs.getInt(1) > 0;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return isAssigned;
}

}
