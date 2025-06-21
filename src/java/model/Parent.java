/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


public class Parent {
    private int id;
    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private String icNumber;
    private String childName;
    private String childClass;
    private String profilePicture; 
    
public Parent() {}
    // Constructor
    public Parent(String name, String email, String password, String contactNumber, String icNumber, String childName, String childClass) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.childName = childName;
        this.childClass = childClass;
    }
    public Parent(String name, String email, String password, String contactNumber, String icNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        
    }

    public Parent(int id, String name, String email, String password, String contactNumber, String icNumber, String childName, String childClass) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.childName = childName;
        this.childClass = childClass;
    }
    
    public Parent(int id, String name, String email, String password, String contactNumber, String icNumber, String childName, String childClass, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.childName = childName;
        this.childClass = childClass;
        this.profilePicture= profilePicture;
        
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    /**
     * @return the childClass
     */
    public String getChildClass() {
        return childClass;
    }

    /**
     * @param childClass the childClass to set
     */
    public void setChildClass(String childClass) {
        this.childClass = childClass;
    }

    /**
     * @return the profilePicture
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * @param profilePicture the profilePicture to set
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
}
