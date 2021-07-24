package com.bezkoder.spring.jwt.mongodb.models;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resources")
public class ResourceFileStream {

    @Id
    private String id;

    @NotBlank
    @Size(min = 3)
    private String filename;

    private List<String> tags;

    @NotBlank
    private String path;

    public ResourceFileStream(String id, @NotBlank @Size(min = 3) String filename, List<String> tags,
            @NotBlank String pathFile) {
        this.id = id;
        this.filename = filename;
        this.tags = tags;
        this.path = pathFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPath() {
        return path;
    }

    public void setPathFile(String path) {
        this.path = path;
    }

}