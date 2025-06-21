package model;

/**
 * Represents a Student entity.
 */
public class Student {

    private int id;
    private String studentClass;
    private String studentName;
    private String icNumber;
    private String sportTeam;
    private String uniformUnit;
    private String parentEmail;

    // Constructors
    public Student() {
    }

    public Student(String studentClass, String studentName, String icNumber) {
        this.studentClass = studentClass;
        this.studentName = studentName;
        this.icNumber = icNumber;
    }

    public Student(String studentClass, String studentName, String icNumber, String sportTeam, String uniformUnit) {
        this.studentClass = studentClass;
        this.studentName = studentName;
        this.icNumber = icNumber;
        this.sportTeam = sportTeam;
        this.uniformUnit = uniformUnit;
    }
    public Student(String studentClass, String studentName, String icNumber, String sportTeam, String uniformUnit, String parentEmail) {
        this.studentClass = studentClass;
        this.studentName = studentName;
        this.icNumber = icNumber;
        this.sportTeam = sportTeam;
        this.uniformUnit = uniformUnit;
        this.parentEmail = parentEmail; 
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    /**
     * @return the sportTeam
     */
    public String getSportTeam() {
        return sportTeam;
    }

    /**
     * @param sportTeam the sportTeam to set
     */
    public void setSportTeam(String sportTeam) {
        this.sportTeam = sportTeam;
    }

    /**
     * @return the uniformUnit
     */
    public String getUniformUnit() {
        return uniformUnit;
    }

    /**
     * @param uniformUnit the uniformUnit to set
     */
    public void setUniformUnit(String uniformUnit) {
        this.uniformUnit = uniformUnit;
    }

    /**
     * @return the parentEmail
     */
    public String getParentEmail() {
        return parentEmail;
    }

    /**
     * @param parentEmail the parentEmail to set
     */
    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
    

}
