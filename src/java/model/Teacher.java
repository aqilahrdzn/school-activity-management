package model;

public class Teacher {

    private int id;
    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private String icNumber;
    private String role;
    private String profilePicture; // ✅ New field added
    private String isGuruKelas;
    private String kelas;
    private int assignedYear;

    // Constructors
    public Teacher() {
        // No-arg constructor
    }

    public Teacher(String name, String email, String password, String contactNumber, String icNumber, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.role = role;
    }

    public Teacher(int id, String name, String email, String password, String contactNumber, String icNumber, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.role = role;
    }

    public Teacher(int id, String name, String email, String password, String contactNumber, String icNumber, String role, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.icNumber = icNumber;
        this.role = role;
        this.profilePicture = profilePicture;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ✅ Getter and Setter for profilePicture
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    // Add getters and setters

    public String getIsGuruKelas() {
        return isGuruKelas;
    }

    public void setIsGuruKelas(String isGuruKelas) {
        this.isGuruKelas = isGuruKelas;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    /**
     * @return the assignedYear
     */
    public int getAssignedYear() {
        return assignedYear;
    }

    /**
     * @param assignedYear the assignedYear to set
     */
    public void setAssignedYear(int assignedYear) {
        this.assignedYear = assignedYear;
    }
}
