package com.example.CIRS.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.CIRS.model.Report;
import com.example.CIRS.model.ReportStatus;
import com.example.CIRS.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Create a new report (multipart form with optional file)
    @PostMapping
    public ResponseEntity<Report> createReport(@RequestParam String title,
                                               @RequestParam String description,
                                               @RequestParam String username,
                                               @RequestParam(required = false) MultipartFile file) throws IOException {
        Report saved = reportService.createReport(title, description, username, file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReport(@PathVariable Long id) {
        return reportService.getReport(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Report>> getReportsByUser(@PathVariable String username) {
        return ResponseEntity.ok(reportService.getReportsByUser(username));
    }

    // Update status and optional assignee
    @PutMapping("/{id}/status")
    public ResponseEntity<Report> updateStatus(@PathVariable Long id,
                                               @RequestParam ReportStatus status,
                                               @RequestParam(required = false) String assignedTo) {
        Report updated = reportService.updateStatus(id, status, assignedTo);
        return ResponseEntity.ok(updated);
    }
}
