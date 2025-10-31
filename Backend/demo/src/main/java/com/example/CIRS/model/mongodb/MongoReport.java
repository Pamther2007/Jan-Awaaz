package com.example.CIRS.model.mongodb;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.CIRS.model.ReportStatus;

@Document(collection = "reports")
public class MongoReport {

    @Id
    private String id;

    private String title;
    private String description;
    private String imageLink;
    private String createdBy;
    private String assignedTo;
    private String previousReportDetails;
    private ReportStatus status = ReportStatus.PENDING;
    private Instant createdAt;
    private Instant updatedAt;

    public MongoReport() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public MongoReport(String title, String description, String imageLink, String createdBy, String previousReportDetails) {
        this();
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
        this.createdBy = createdBy;
        this.previousReportDetails = previousReportDetails;
        this.status = ReportStatus.PENDING;
    }

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }
    public String getPreviousReportDetails() { return previousReportDetails; }
    public void setPreviousReportDetails(String previousReportDetails) { this.previousReportDetails = previousReportDetails; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}