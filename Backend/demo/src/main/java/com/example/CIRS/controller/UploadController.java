package com.example.CIRS.controller;

import java.io.IOException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;

@Controller
public class UploadController {

    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public UploadController(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(), 
                file.getOriginalFilename(), 
                file.getContentType()
            );

            return ResponseEntity.ok(fileId.toString());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/images/{fileId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileId) {
        // Try to find the file; if not found, return 404.
            ResponseEntity<byte[]> response = ResponseEntity.notFound().build();
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
            if (file != null) {
                GridFsResource resource = gridFsTemplate.getResource(file);
                try {
                    // Determine content type from metadata (fallback to octet-stream)
                    String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                    if (file.getMetadata() != null && file.getMetadata().containsKey("_contentType")) {
                        contentType = file.getMetadata().getString("_contentType");
                    }

                    byte[] bytes = resource.getContent().readAllBytes();
                    response = ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(bytes);
                } catch (IOException e) {
                    response = ResponseEntity.internalServerError().build();
                }
            }

            return response;
    }
}