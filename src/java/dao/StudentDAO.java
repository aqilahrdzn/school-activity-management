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
 * Handles database operations for the Student entity.
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
                    // Assuming a constructor Student(class, name, ic) exists
                    return new Student(studentClass, studentName, icNumber);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                    student.setId(rs.getInt("id"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setIcNumber(rs.getString("ic_number"));
                    student.setStudentClass(rs.getString("class"));
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
                student.setStudentClass(rs.getString("class"));
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
        return null;
    }

    public Student getStudentByIC(String icNumber) {
        Student student = null;
        String query = "SELECT id, class, student_name, ic_number, sport_team, uniform_unit FROM student WHERE ic_number = ?";
        try (Connection con = DBConfig.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, icNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentClass(rs.getString("class"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setIcNumber(rs.getString("ic_number"));
                    student.setSportTeam(rs.getString("sport_team"));
                    student.setUniformUnit(rs.getString("uniform_unit"));

                    String parentEmail = getParentEmailByIc(icNumber);
                    student.setParentEmail(parentEmail);
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
        String sql = "SELECT * FROM student WHERE class = ? AND status = 'active'";
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

    // --- NEW METHODS REQUIRED BY EVENTCONTROLLER ---
    /**
     * Retrieves a list of all student IC numbers for a given list of class
     * names. Required by EventController for adding participants by class.
     *
     * @param classesList A list of class names (e.g., ["1 Makkah", "1
     * Madinah"]).
     * @return A List of student IC numbers.
     */
    public List<String> getStudentICsByClasses(List<String> classesList) {
        List<String> ics = new ArrayList<>();
        if (classesList == null || classesList.isEmpty()) {
            return ics;
        }
        // Build the SQL query with the correct number of placeholders
        StringBuilder sql = new StringBuilder("SELECT ic_number FROM student WHERE class IN (");
        for (int i = 0; i < classesList.size(); i++) {
            sql.append("?").append(i < classesList.size() - 1 ? "," : "");
        }
        sql.append(")");

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < classesList.size(); i++) {
                stmt.setString(i + 1, classesList.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ics.add(rs.getString("ic_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ics;
    }

    /**
     * Retrieves a list of all student IC numbers for a given sport team.
     * Required by EventController.
     *
     * @param sportTeam The name of the sport team.
     * @return A List of student IC numbers.
     */
    public List<String> getStudentICsBySport(String sportTeam) {
        List<String> ics = new ArrayList<>();
        String sql = "SELECT ic_number FROM student WHERE sport_team = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sportTeam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ics.add(rs.getString("ic_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ics;
    }

    /**
     * Retrieves a list of all student IC numbers for a given uniform unit.
     * Required by EventController.
     *
     * @param uniformUnit The name of the uniform unit.
     * @return A List of student IC numbers.
     */
    public List<String> getStudentICsByUniform(String uniformUnit) {
        List<String> ics = new ArrayList<>();
        String sql = "SELECT ic_number FROM student WHERE uniform_unit = ?";
        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uniformUnit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ics.add(rs.getString("ic_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ics;
    }

    /**
     * Retrieves full Student objects for a given list of IC numbers. Required
     * by EventController to get participant details for PDF generation.
     *
     * @param icList A list of student IC numbers.
     * @return A List of Student objects.
     */
    public List<Student> getStudentsByICs(List<String> icList) {
        List<Student> students = new ArrayList<>();
        if (icList == null || icList.isEmpty()) {
            return students;
        }
        // Build the SQL query with the correct number of placeholders
        StringBuilder sql = new StringBuilder("SELECT * FROM student WHERE ic_number IN (");
        for (int i = 0; i < icList.size(); i++) {
            sql.append("?").append(i < icList.size() - 1 ? "," : "");
        }
        sql.append(")");

        try (Connection conn = DBConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < icList.size(); i++) {
                stmt.setString(i + 1, icList.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentName(rs.getString("student_name"));
                    student.setIcNumber(rs.getString("ic_number"));
                    student.setStudentClass(rs.getString("class"));
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

    // --- END NEW METHODS ---
    public boolean promoteStudents() {
        String selectQuery = "SELECT id, class FROM student";
        String updateQuery = "UPDATE student SET class = ? WHERE id = ?";
        String archiveQuery = "UPDATE student SET status = 'archived' WHERE id = ?";  // Query to archive Year 6 students

        try (
                Connection conn = DBConfig.getConnection(); PreparedStatement selectStmt = conn.prepareStatement(selectQuery); PreparedStatement updateStmt = conn.prepareStatement(updateQuery); PreparedStatement archiveStmt = conn.prepareStatement(archiveQuery); // PreparedStatement for archiving
                 ResultSet rs = selectStmt.executeQuery()) {

            conn.setAutoCommit(false); // Start transaction

            while (rs.next()) {
                int id = rs.getInt("id");
                String studentClass = rs.getString("class");

                if (studentClass == null) {
                    continue;
                }

                String[] parts = studentClass.split(" ", 2);
                if (parts.length == 2) {
                    try {
                        int year = Integer.parseInt(parts[0]);
                        if (year < 6) { // Promote students from Year 1 to 5
                            year++;
                            String newClass = year + " " + parts[1];
                            updateStmt.setString(1, newClass);
                            updateStmt.setInt(2, id);
                            updateStmt.addBatch();
                        } else if (year == 6) {  // Archive students in Year 6
                            archiveStmt.setInt(1, id);
                            archiveStmt.addBatch();
                        }
                    } catch (NumberFormatException e) {
                        // Skip if the class format is incorrect (e.g., "Tadika")
                        continue;
                    }
                }
            }

            // Execute all the batch operations
            updateStmt.executeBatch();
            archiveStmt.executeBatch();  // Execute the archive batch
            conn.commit(); // Commit transaction
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Consider rolling back transaction in a real-world scenario
            return false;
        }
    }

    public boolean archiveStudent(String icNumber) throws SQLException {
        String sql = "UPDATE student SET status = 'archived' WHERE ic_number = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        int rowsAffected = 0;

        try {
            conn = DBConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, icNumber);
            rowsAffected = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            throw e; // Re-throw to be handled by the servlet
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return rowsAffected > 0;

    }
}
