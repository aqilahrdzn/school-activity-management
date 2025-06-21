package dao;

import model.Student;
import util.DBConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles operations for student data.
 */
public class StudentDAO {

    public boolean registerStudent(Student student) {
        String sql = "INSERT INTO student (class, student_name, ic_number, sport_team, uniform_unit) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentClass());
            stmt.setString(2, student.getStudentName());
            stmt.setString(3, student.getIcNumber());
            stmt.setString(4, student.getSportTeam());
            stmt.setString(5, student.getUniformUnit());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean studentExists(String studentClass, String studentName, String icNumber) {
        String sql = "SELECT 1 FROM student WHERE class = ? AND student_name = ? AND ic_number = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentClass);
            stmt.setString(2, studentName);
            stmt.setString(3, icNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student getChildDetailsByIC(String icNumber) {
        String sql = "SELECT student_name, class FROM student WHERE ic_number = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, icNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String studentName = rs.getString("student_name");
                    String studentClass = rs.getString("class");
                    return new Student(studentClass, studentName, icNumber);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no record is found
    }

    public void linkChildToParent(String childIc, int parentId) {
        String query = "UPDATE student SET parent_id = ? WHERE ic_number = ?";
        try (Connection connection = DBConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, parentId);
            preparedStatement.setString(2, childIc);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getStudentNamesByClass(String className) {
        List<String> names = new ArrayList<>();
        String sql = "SELECT student_name FROM student WHERE class = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("student_name");
                if (name != null && !name.trim().isEmpty()) {
                    names.add(name.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public List<Student> getStudentsByParentId(int parentId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE parent_id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setStudentName(rs.getString("student_name")); // Adjust column names accordingly
                    student.setStudentClass(rs.getString("class")); // Adjust column names accordingly
                    // Set other fields as needed, e.g., student ID, IC, etc.
                    students.add(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM student WHERE id = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentName(rs.getString("student_name"));
                student.setIcNumber(rs.getString("ic_number"));
                student.setSportTeam(rs.getString("sport_team"));
                student.setUniformUnit(rs.getString("uniform_unit"));
                student.setStudentClass(rs.getString("class")); // Important for authorization check
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE student SET student_name=?, ic_number=?, sport_team=?, uniform_unit=?, class=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentName());
            stmt.setString(2, student.getIcNumber());
            stmt.setString(3, student.getSportTeam());
            stmt.setString(4, student.getUniformUnit());
            stmt.setString(5, student.getStudentClass());
            stmt.setInt(6, student.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getStudentsByEvent(int eventId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM student s "
                + "JOIN event_participants ep ON s.ic_number = ep.student_ic "
                + "WHERE ep.event_id = ?";

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentClass(rs.getString("class"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setIcNumber(rs.getString("ic_number"));
                    student.setSportTeam(rs.getString("sport_team"));
                    student.setUniformUnit(rs.getString("uniform_unit"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public String getParentEmailByIc(String icNumber) {
        String sql = "SELECT p.email FROM student s JOIN parent p ON s.parent_id = p.id WHERE s.ic_number = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, icNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // return null if not found
    }

    public Student getStudentByIC(String icNumber) {
        Student student = null;
        // This query fetches all student details
        String query = "SELECT id, class, student_name, ic_number, sport_team, uniform_unit FROM student WHERE ic_number = ?";

        try (Connection con = DBConfig.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, icNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentClass(rs.getString("class")); // Use 'class' as per your schema
                    student.setStudentName(rs.getString("student_name"));
                    student.setIcNumber(rs.getString("ic_number"));
                    student.setSportTeam(rs.getString("sport_team"));
                    student.setUniformUnit(rs.getString("uniform_unit"));

                    // --- NEW: Fetch parent's email using the existing method ---
                    String parentEmail = getParentEmailByIc(icNumber); // Call your existing method
                    student.setParentEmail(parentEmail);
                    // --- END NEW ---
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getStudentByIC: " + e.getMessage());
            e.printStackTrace();
        }
        return student;
    }

    public List<Student> getStudentsByClass(String className) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE class = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentName(rs.getString("student_name"));
                student.setIcNumber(rs.getString("ic_number"));
                student.setSportTeam(rs.getString("sport_team"));
                student.setUniformUnit(rs.getString("uniform_unit"));
                student.setStudentClass(rs.getString("class"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    public boolean promoteStudents() {
    String selectQuery = "SELECT id, class FROM student";
    String updateQuery = "UPDATE student SET class = ? WHERE id = ?";
    try (
        Connection conn = DBConfig.getConnection();
        PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        ResultSet rs = selectStmt.executeQuery()
    ) {
        while (rs.next()) {
            int id = rs.getInt("id");
            String studentClass = rs.getString("class");

            String[] parts = studentClass.split(" ", 2); // Split into [year, name]
            if (parts.length == 2) {
                try {
                    int year = Integer.parseInt(parts[0]);
                    if (year < 6) { // Promote only if year < 6
                        year++; // Increment year
                        String newClass = year + " " + parts[1];
                        updateStmt.setString(1, newClass);
                        updateStmt.setInt(2, id);
                        updateStmt.addBatch();
                    } else {
                        // Optional: delete or archive students who finished Year 6
                        // You can implement this if needed
                    }
                } catch (NumberFormatException e) {
                    // Skip if class format is incorrect
                    continue;
                }
            }
        }
        updateStmt.executeBatch();
        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}
