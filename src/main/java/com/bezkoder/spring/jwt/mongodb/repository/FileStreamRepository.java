package com.bezkoder.spring.jwt.mongodb.repository;

import java.util.List;
import java.util.Optional;

import com.bezkoder.spring.jwt.mongodb.models.ResourceFileStream;
import com.bezkoder.spring.jwt.mongodb.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileStreamRepository extends MongoRepository<ResourceFileStream, String> {

    // Optional<ResourceFileStream> findByPath(String path);

    Optional<List<ResourceFileStream>> findByAuthor(User author);

    Boolean existsByPath();

    List<ResourceFileStream> findByFilenameContaining(String filename);

    Optional<List<ResourceFileStream>> findByFilename(String filename);

}