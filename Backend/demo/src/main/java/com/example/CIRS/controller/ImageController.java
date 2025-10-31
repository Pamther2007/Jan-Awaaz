package com.example.CIRS.controller;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.mongodb.gridfs.GridFsResource;

import com.example.CIRS.service.GridFsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@RestController
@RequestMapping("/images")
@ConditionalOnBean(name = "gridFsService")
public class ImageController {

    private final GridFsService gridFsService;

    public ImageController(GridFsService gridFsService) {
        this.gridFsService = gridFsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String id = gridFsService.storeFile(file);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to store file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> serve(@PathVariable String id) throws IOException {
        return gridFsService.loadAsResource(id)
            .map(res -> {
                try {
                    InputStreamResource isr = new InputStreamResource(res.getInputStream());
                    String contentType = res.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : res.getContentType();
                    return ResponseEntity.ok()
                        .contentLength(res.contentLength())
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(isr);
                } catch (IOException e) {
                    return ResponseEntity.internalServerError().build();
                }
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
