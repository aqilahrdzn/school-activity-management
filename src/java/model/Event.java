package model;

public class Event {
    private String id;  // Added id attribute
    private String category;
    private String title;
    private String description;
    private String startTime;
    private String endTime;
    private String timeZone;
    private String targetClass;
    private String posterPath;
    private String suratPengesahan;
    private String createdBy;

public String getCreatedBy() {
    return createdBy;
}

public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
}


    // Getter and setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for startTime
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    // Getter and setter for endTime
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // Getter and setter for timeZone
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    // Getter and setter for targetClass
    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    // Getter and setter for posterPath
    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the suratPengesahan
     */
    public String getSuratPengesahan() {
        return suratPengesahan;
    }

    /**
     * @param suratPengesahan the suratPengesahan to set
     */
    public void setSuratPengesahan(String suratPengesahan) {
        this.suratPengesahan = suratPengesahan;
    }
}
