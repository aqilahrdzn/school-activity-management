/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private String message;
    private Timestamp createdAt;
    private int isRead;
    private int eventId;
    private String role;

public int getEventId() {
    return eventId;
}

public void setEventId(int eventId) {
    this.eventId = eventId;
}


public int getIsRead() {
    return isRead;
}

public void setIsRead(int isRead) {
    this.isRead = isRead;
}


    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
