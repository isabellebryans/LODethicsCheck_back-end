package Utilities;

// FOOPS Check class for FOOPS response
public class FoopsCheck {
    private String id;
    private String principleId;
    private String categoryId;
    private String status;
    private String title;
    private String explanation;
    private String description;
    private int totalPassedTests;
    private int totalTestsRun;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrincipleId() {
        return principleId;
    }

    public void setPrincipleId(String principleId) {
        this.principleId = principleId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalPassedTests() {
        return totalPassedTests;
    }

    public void setTotalPassedTests(int totalPassedTests) {
        this.totalPassedTests = totalPassedTests;
    }

    public int getTotalTestsRun() {
        return totalTestsRun;
    }

    public void setTotalTestsRun(int totalTestsRun) {
        this.totalTestsRun = totalTestsRun;
    }
}
