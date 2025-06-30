package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Parent;
import model.Student;
import util.DBConfig;

public class ParentDAO {

    public boolean isEmailOrICNumberExists(String email, String icNumber) {
        String query = "SELECT * FROM parent WHERE email = ? OR ic_number = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, icNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Error checking for duplicate email or IC number: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Parent getParentByEmail(String email) {
        Parent parent = null;
        String query = "SELECT id, name, email, contact_number, profile_picture, password FROM parent WHERE email = ?";

        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                parent = new Parent();
                parent.setId(resultSet.getInt("id"));
                parent.setName(resultSet.getString("name"));
                parent.setEmail(resultSet.getString("email"));
                parent.setContactNumber(resultSet.getString("contact_number"));
                parent.setProfilePicture(resultSet.getString("profile_picture"));
                parent.setPassword(resultSet.getString("password")); // ✅ Add this line
            }
        } catch (SQLException e) {
            System.err.println("SQL error in getParentByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return parent;
    }

    public List<Student> getChildrenByParentEmail(String email) {
        List<Student> children = new ArrayList<>();
        String query = "SELECT s.student_name, s.class "
                + "FROM student s "
                + "INNER JOIN parent p ON s.parent_id = p.id "
                + "WHERE p.email = ?";

        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Student student = new Student();
                student.setStudentName(resultSet.getString("student_name"));
                student.setStudentClass(resultSet.getString("class"));
                children.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return children;
    }

    public boolean insertParentWithChildren(Parent parent, List<Student> children) {
        String parentQuery = "INSERT INTO parent (name, email, password, contact_number, ic_number, profile_picture) VALUES (?, ?, ?, ?, ?, ?)";
        String childQuery = "INSERT INTO student (parent_id, student_name, class, ic_number) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConfig.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement parentStmt = connection.prepareStatement(parentQuery, Statement.RETURN_GENERATED_KEYS)) {
                parentStmt.setString(1, parent.getName());
                parentStmt.setString(2, parent.getEmail());
                parentStmt.setString(3, parent.getPassword());
                parentStmt.setString(4, parent.getContactNumber());
                parentStmt.setString(5, parent.getIcNumber());
                parentStmt.setString(6, parent.getProfilePicture());
                parentStmt.executeUpdate();

                ResultSet rs = parentStmt.getGeneratedKeys();
                if (rs.next()) {
                    int parentId = rs.getInt(1);

                    try (PreparedStatement childStmt = connection.prepareStatement(childQuery)) {
                        for (Student child : children) {
                            childStmt.setInt(1, parentId);
                            childStmt.setString(2, child.getStudentName());
                            childStmt.setString(3, child.getStudentClass());
                            childStmt.setString(4, child.getIcNumber());
                            childStmt.addBatch();
                        }
                        childStmt.executeBatch();
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int insertParentAndGetId(Parent parent) {
        String query = "INSERT INTO parent (name, email, password, contact_number, ic_number, profile_picture) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, parent.getName());
            preparedStatement.setString(2, parent.getEmail());
            preparedStatement.setString(3, parent.getPassword());
            preparedStatement.setString(4, parent.getContactNumber());
            preparedStatement.setString(5, parent.getIcNumber());
            preparedStatement.setString(6, parent.getProfilePicture());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Map<Student, List<Map<String, String>>> getStudentEventsByParentEmail(String parentEmail) throws SQLException {
        Map<Student, List<Map<String, String>>> result = new HashMap<>();
        String sql = "SELECT s.*, e.id AS eventId, e.title, e.surat_pengesahan FROM parent p "
                + "JOIN student s ON p.id = s.parent_id "
                + "LEFT JOIN event_participants ep ON s.ic_number = ep.student_ic "
                + "LEFT JOIN events e ON ep.event_id = e.id "
                + "WHERE p.email = ?";

        try (Connection con = DBConfig.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, parentEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setStudentClass(rs.getString("class"));
                    student.setIcNumber(rs.getString("ic_number"));

                    Map<String, String> event = new HashMap<>();
                    event.put("title", rs.getString("title"));
                    event.put("surat_pengesahan", rs.getString("surat_pengesahan"));
                    event.put("id", rs.getString("eventId"));

                    // Replace computeIfAbsent with this:
                    List<Map<String, String>> events = result.get(student);
                    if (events == null) {
                        events = new ArrayList<>();
                        result.put(student, events);
                    }
                    events.add(event);
                }
            }
        }
        return result;
    }

    public Parent getParentByStudentIc(String studentIc) {
        Parent parent = null;
        String sql = "SELECT p.* FROM parent p "
                + "JOIN student s ON p.id = s.parent_id "
                + "WHERE s.ic_number = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentIc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    parent = new Parent();
                    parent.setId(rs.getInt("id"));
                    parent.setName(rs.getString("name"));
                    parent.setEmail(rs.getString("email"));
                    parent.setPassword(rs.getString("password"));
                    parent.setContactNumber(rs.getString("contact_number"));
                    parent.setIcNumber(rs.getString("ic_number"));
                    parent.setProfilePicture(rs.getString("profile_picture"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parent;
    }

    public Parent getParentByStudentId(int studentId) {
        Parent parent = null;
        String sql = "SELECT p.* FROM parent p "
                + "JOIN student s ON p.id = s.parent_id "
                + "WHERE s.id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                parent = new Parent();
                parent.setId(rs.getInt("id"));
                parent.setName(rs.getString("name"));
                parent.setEmail(rs.getString("email"));
                parent.setPassword(rs.getString("password"));
                parent.setContactNumber(rs.getString("contact_number"));
                parent.setIcNumber(rs.getString("ic_number"));
                parent.setProfilePicture(rs.getString("profile_picture"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parent;
    }

}
