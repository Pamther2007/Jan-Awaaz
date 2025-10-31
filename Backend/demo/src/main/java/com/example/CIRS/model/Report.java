package com.example.CIRS.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;


    // Link to image in MongoDB GridFS
    private String imageLink;


    // username of the creator
    private String createdBy;

    // username of person currently responsible
    private String assignedTo;

    // Previous report details (updated after each report)
    @Column(length = 2000)
    private String previousReportDetails;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    private Instant createdAt;
    private Instant updatedAt;

    public Report() {}


    public Report(String title, String description, String imageLink, String createdBy, String previousReportDetails) {
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
        this.createdBy = createdBy;
        this.previousReportDetails = previousReportDetails;
        this.status = ReportStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = Instant.now(); }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}
