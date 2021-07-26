package com.bezkoder.spring.jwt.mongodb.controllers;

import java.util.List;

import com.bezkoder.spring.jwt.mongodb.Exceptions.FileStreamNotFoundException;
import com.bezkoder.spring.jwt.mongodb.Exceptions.UserNotFoundException;
import com.bezkoder.spring.jwt.mongodb.models.ResourceFileStream;
import com.bezkoder.spring.jwt.mongodb.models.User;
import com.bezkoder.spring.jwt.mongodb.repository.FileStreamRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;

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
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    /*
     * @Autowired private NonStaticResourceHttpRequestHandler
     * nonStaticResourceHttpRequestHandler;
     */

    @GetMapping("/{filename}")
    public ResponseEntity<?> getVideosAudiosByName(@PathVariable("filename") String filename) {

        List<ResourceFileStream> files = fileStreamRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException(
                        "Sorry :: Something gone wrong :: this filename {" + filename + "} does'nt exists"));

        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    // /api/audiovideo/all?tags=...
    @GetMapping("/all")
    public ResponseEntity<?> getVideosAudiosByTags(@RequestParam("tags") List<String> tags) {

        BasicQuery query = new BasicQuery("{ tags : { $in : " + tags + "}}");
        List<ResourceFileStream> files = mongoTemplate.find(query, ResourceFileStream.class);

        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    // /api/audiovideo/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoAudioById(@PathVariable("id") String id) {

        ResourceFileStream file = fileStreamRepository.findById(id).orElseThrow(() -> new FileStreamNotFoundException(
                "Sorry :: Something gone wrong :: this File Stream Id {" + id + "} does'nt exists"));

        return new ResponseEntity<ResourceFileStream>(file, HttpStatus.OK);
    }

    // /api/audiovideo/author?id=...
    @GetMapping("/author")
    public ResponseEntity<?> getVideosAudiosByAuthor(@RequestParam("id") String id) {

        /*
         * METHOD 1 BasicQuery query = new BasicQuery("{ author : " + id + "}");
         * List<ResourceFileStream> files = mongoTemplate.find(query,
         * ResourceFileStream.class);
         */
        // METHOD 2
        User author = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                "Sorry :: Something gone wrong :: this User Id {" + id + "} does'nt exists"));

        List<ResourceFileStream> files = fileStreamRepository.findByAuthor(author)
                .orElseThrow(() -> new RuntimeException(
                        "Sorry :: Something gone wrong : There's no Resource files with this Author Id {" + id + "} "));
        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }
    // deux end point download upload ....

}