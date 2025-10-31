package com.example.CIRS.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

@Service
@ConditionalOnBean(name = "gridFsTemplate")
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFsService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String storeFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Object id = gridFsTemplate.store(is, file.getOriginalFilename(), file.getContentType());
            return id == null ? null : id.toString();
        }
    }

    public Optional<GridFsResource> loadAsResource(String id) {
    GridFSFile gfile = gridFsTemplate.findOne(Query.query(GridFsCriteria.where("_id").is(new ObjectId(id))));
    GridFsResource resource = gridFsTemplate.getResource(gfile);
    if (resource == null || gfile == null) return Optional.empty();
    return Optional.of(resource);
    }
}
