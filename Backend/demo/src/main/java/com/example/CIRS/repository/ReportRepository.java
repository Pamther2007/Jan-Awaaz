package com.example.CIRS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CIRS.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreatedBy(String username);
}
