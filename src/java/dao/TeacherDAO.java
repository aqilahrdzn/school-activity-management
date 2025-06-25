package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Teacher;
import util.DBConfig;

public class TeacherDAO {

    // Get teacher details by email (including profile picture)
    public Teacher getTeacherDetails(String email) {
        String query = "SELECT * FROM teachers WHERE email = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

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

    public boolean insertTeacher(Teacher teacher) {
        String query = "INSERT INTO teachers (name, email, password, contact_number, ic_number, role, profile_picture, is_guru_kelas, kelas, assigned_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConfig.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, teacher.getName());
            ps.setString(2, teacher.getEmail());
            ps.setString(3, teacher.getPassword());
            ps.setString(4, teacher.getContactNumber());
            ps.setString(5, teacher.getIcNumber());
            ps.setString(6, teacher.getRole());
            ps.setString(7, teacher.getProfilePicture());
            ps.setString(8, teacher.getIsGuruKelas());

            if (teacher.getKelas() == null || teacher.getKelas().isEmpty()) {
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else {
                ps.setString(9, teacher.getKelas());
            }

            ps.setInt(10, teacher.getAssignedYear()); // ✅ Final fix

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Teacher authenticateTeacher(String email, String password) {
        String sql = "SELECT id, name, email, password, contact_number, ic_number, role, is_guru_kelas, kelas FROM teacher WHERE email = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public boolean isClassAlreadyAssigned(String kelas, int assignedYear, int currentTeacherId) {
        String sql = "SELECT id FROM teachers WHERE kelas = ? AND assigned_year = ? AND id != ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kelas);
            stmt.setInt(2, assignedYear);
            stmt.setInt(3, currentTeacherId);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true = already assigned to someone else

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAssignedClasses() {
        List<String> assignedClasses = new ArrayList<>();
        String sql = "SELECT kelas FROM teachers WHERE kelas IS NOT NULL AND kelas != ''";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                assignedClasses.add(rs.getString("kelas"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignedClasses;
    }

    public List<Teacher> getTeachersByRole(String role) {
        List<Teacher> teacherList = new ArrayList<>();
        String query = "SELECT * FROM teachers WHERE role = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, role);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(resultSet.getInt("id"));
                teacher.setName(resultSet.getString("name"));
                teacher.setEmail(resultSet.getString("email"));
                teacher.setContactNumber(resultSet.getString("contact_number"));
                teacher.setRole(resultSet.getString("role"));
                teacher.setProfilePicture(resultSet.getString("profile_picture"));
                teacher.setIsGuruKelas(resultSet.getString("is_guru_kelas"));
                teacher.setKelas(resultSet.getString("kelas"));
                teacher.setPassword(resultSet.getString("password"));
                teacher.setAssignedYear(resultSet.getInt("assigned_year"));
                teacherList.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacherList;
    }

    public boolean updateTeacherClass(int teacherId, String kelas, int assignedYear) {
        String sql = "UPDATE teachers SET kelas = ?, assigned_year = ? WHERE id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kelas);
            stmt.setInt(2, assignedYear);
            stmt.setInt(3, teacherId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTeacher(int teacherId) {
        String query = "DELETE FROM teachers WHERE id = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, teacherId);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Teacher getTeacherByClass(String className) {
        Teacher teacher = null;
        String sql = "SELECT * FROM teachers WHERE kelas = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    teacher = new Teacher();
                    teacher.setId(rs.getInt("id"));
                    teacher.setName(rs.getString("name"));
                    teacher.setEmail(rs.getString("email"));
                    teacher.setContactNumber(rs.getString("contact_number"));
                    teacher.setRole(rs.getString("role"));
                    teacher.setProfilePicture(rs.getString("profile_picture"));
                    teacher.setIsGuruKelas(rs.getString("is_guru_kelas"));
                    teacher.setKelas(rs.getString("kelas"));
                    teacher.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }

    public boolean isClassAssignedToGuruKelas(String className) {
        String sql = "SELECT COUNT(*) FROM teachers WHERE kelas = ? AND is_guru_kelas = 'Yes'";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, className);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If count > 0, the class is taken.
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return false in case of an error, so the form doesn't lock up.
        return false;
    }
}
