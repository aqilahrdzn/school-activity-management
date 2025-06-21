/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Lenovo
 */
public class Reminder {
    private int id;
    private String activityName;
    private String activityDate;
    private String reminderDetails;

    // Constructors
    public Reminder() {}

    public Reminder(String activityName, String activityDate, String reminderDetails) {
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.reminderDetails = reminderDetails;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getReminderDetails() {
        return reminderDetails;
    }

    public void setReminderDetails(String reminderDetails) {
        this.reminderDetails = reminderDetails;
    }
}

