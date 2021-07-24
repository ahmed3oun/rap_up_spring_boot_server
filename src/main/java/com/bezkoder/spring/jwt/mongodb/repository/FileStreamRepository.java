package com.bezkoder.spring.jwt.mongodb.repository;

import java.util.List;
import java.util.Optional;

import com.bezkoder.spring.jwt.mongodb.models.ResourceFileStream;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileStreamRepository extends MongoRepository<ResourceFileStream, String> {

    Optional<ResourceFileStream> findByPath(String path);

    Boolean existsByPath();

    Optional<List<ResourceFileStream>> findByFilename(String filename);

}