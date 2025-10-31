package com.example.CIRS.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.CIRS.model.Report;
import com.example.CIRS.model.ReportStatus;
import com.example.CIRS.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final GridFsService gridFsService;

    @Autowired
    public ReportService(ReportRepository reportRepository, GridFsService gridFsService) {
        this.reportRepository = reportRepository;
        this.gridFsService = gridFsService;
    }

    public Report createReport(String title, String description, String createdBy, MultipartFile file) throws IOException {
        String imageLink = null;
        if (file != null && !file.isEmpty()) {
            imageLink = gridFsService.storeFile(file);
        }
        // For new report, previousReportDetails can be empty or fetched from last report if needed
        Report r = new Report(title, description, imageLink, createdBy, "");
        return reportRepository.save(r);
    }

    public Optional<Report> getReport(Long id) { return reportRepository.findById(id); }

    public List<Report> getReportsByUser(String username) { return reportRepository.findByCreatedBy(username); }

    public Report updateStatus(Long id, ReportStatus status, String assignedTo) {
        Report r = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        r.setStatus(status);
        if (assignedTo != null) r.setAssignedTo(assignedTo);
        return reportRepository.save(r);
    }
}
