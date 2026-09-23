package model;

public class Test {
    private final int testId;
    private final String title;
    private final String description;
    private final String category;
    private final int durationMinutes;
    private final int createdBy;
    private final boolean active;

    public Test(int testId, String title, String description,
                int durationMinutes, int createdBy, boolean active) {
        this(testId, title, description, "General",
                durationMinutes, createdBy, active);
    }

    public Test(int testId, String title, String description,
                String category, int durationMinutes,
                int createdBy, boolean active) {
        this.testId = testId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.createdBy = createdBy;
        this.active = active;
    }

    public int getTestId() {
        return testId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public boolean isActive() {
        return active;
    }
}