package com.bezkoder.spring.jwt.mongodb.controllers;

import java.util.List;

import com.bezkoder.spring.jwt.mongodb.models.ResourceFileStream;
import com.bezkoder.spring.jwt.mongodb.repository.FileStreamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/audiovideo")
public class StreamAudioVideoController {

    public static final String VIDEO_PATH = "/static/videos";
    public static final String AUDIO_PATH = "/static/audios";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    @Autowired
    private FileStreamRepository fileStreamRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("/{filename}")
    public ResponseEntity<?> getVideosAudiosByName(@PathVariable("filename") String filename) {

        List<ResourceFileStream> files = fileStreamRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException(
                        "Sorry :: Something gone wrong :: this filename {" + filename + "} does'nt exists"));

        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getVideosAudiosByTags(@RequestParam("tags") List<String> tags) {

        BasicQuery query = new BasicQuery("{ tags : { $in : " + tags + "}}");
        List<ResourceFileStream> files = mongoTemplate.find(query, ResourceFileStream.class);

        /*
         * List<ResourceFileStream> files = fileStreamRepository.findAll(query,
         * ResourceFileStream.class) .orElseThrow(() -> new RuntimeException(
         * "Sorry :: Something gone wrong :: this filename {" + filename +
         * "} does'nt exists"));
         */
        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideosAudiosById(@PathVariable("id") String id) {

        ResourceFileStream files = fileStreamRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Sorry :: Something gone wrong :: this Id {" + id + "} does'nt exists"));

        return new ResponseEntity<ResourceFileStream>(files, HttpStatus.OK);
    }

    // deux end point download upload ....


}