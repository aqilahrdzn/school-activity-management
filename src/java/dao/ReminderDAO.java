/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import util.DBConfig;
import model.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReminderDAO {
    public boolean saveReminder(Reminder reminder) {
        String sql = "INSERT INTO reminders (activity_name, activity_date, reminder_details) VALUES (?, ?, ?)";

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, reminder.getActivityName());
            statement.setString(2, reminder.getActivityDate());
            statement.setString(3, reminder.getReminderDetails());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}