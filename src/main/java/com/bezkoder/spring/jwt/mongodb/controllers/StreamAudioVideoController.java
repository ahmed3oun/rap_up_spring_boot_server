package com.bezkoder.spring.jwt.mongodb.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.bezkoder.spring.jwt.mongodb.Exceptions.FileStreamNotFoundException;
import com.bezkoder.spring.jwt.mongodb.Exceptions.ForbiddenMimeTypeException;
import com.bezkoder.spring.jwt.mongodb.Exceptions.UserNotFoundException;
import com.bezkoder.spring.jwt.mongodb.models.ResourceFileStream;
import com.bezkoder.spring.jwt.mongodb.models.User;
import com.bezkoder.spring.jwt.mongodb.payload.response.MessageResponse;
import com.bezkoder.spring.jwt.mongodb.repository.FileStreamRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/audiovideo")
public class StreamAudioVideoController {

    // public static final String VIDEO_PATH =
    // "C:\\Users\\Ahmed\\Desktop\\ahmed\\SpringProjects\\spring-boot-security-jwt-rapup\\src\\main\\resources\\static\\videos";
    public static final String VIDEO_PATH = "..\\..\\..\\..\\..\\..\\..\\resources\\static\\videos";
    // public static final String AUDIO_PATH =
    // "C:\\Users\\Ahmed\\Desktop\\ahmed\\SpringProjects\\spring-boot-security-jwt-rapup\\src\\main\\resources\\static\\audios";
    public static final String AUDIO_PATH = "..\\..\\..\\..\\..\\..\\..\\resources\\static\\audios";
    public static final int BYTE_RANGE = 128; // increase the byterange from here

    @Autowired
    private FileStreamRepository fileStreamRepository;

    // @Autowired
    // private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    // /api/audiovideo/upload?author_id=...&file=...
    @PostMapping(value = "/upload"/* , consumes = MediaType.APPLICATION_JSON_VALUE */)
    public ResponseEntity<?> uploadResourceFileStream(@RequestParam("author_id") String author_id,
            @RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {

        User author = userRepository.findById(author_id).orElseThrow(() -> new UserNotFoundException(
                "Sorry :: Something gone wrong :: this Author with ID {" + author_id + "} does'nt exists"));
        if (file.getContentType().equalsIgnoreCase("video/mp4")) {

            Path filePath = Paths
                    .get(VIDEO_PATH + "\\" + author_id + "\\" + LocalDate.now().toString() + file.getOriginalFilename())
                    .toAbsolutePath().normalize();
            Path fileStoragePath = Paths.get(VIDEO_PATH + "\\" + author_id).toAbsolutePath().normalize();
            try {
                Files.createDirectories(fileStoragePath);
            } catch (IOException e) {
                throw new RuntimeException("Issue in creating file directory");
            }
            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Issue in storing the file", e);
            }
            // file.transferTo(new File(VIDEO_PATH + "\\" + LocalDateTime.now().toString() +
            // file.getOriginalFilename()));
            ResourceFileStream fileStream = new ResourceFileStream(file.getOriginalFilename(), null,
                    filePath.toString(), author);
            if (fileStreamRepository.save(fileStream) == null) {
                throw new RuntimeException("Sorry :: Something gone wrong :: this File Stream  does'nt Saved !");
            }
            return new ResponseEntity<MessageResponse>(new MessageResponse("Video uploaded succesfully!"),
                    HttpStatus.OK);

        }
        if (file.getContentType().equalsIgnoreCase("audio/mpeg")) {
            // file.transferTo(new File(AUDIO_PATH));
            Path filePath = Paths
                    .get(AUDIO_PATH + "\\" + author_id + "\\" + LocalDate.now().toString() + file.getOriginalFilename())
                    .toAbsolutePath().normalize();
            Path fileStoragePath = Paths.get(AUDIO_PATH + "\\" + author_id).toAbsolutePath().normalize();
            try {
                Files.createDirectories(fileStoragePath);
            } catch (IOException e) {
                throw new RuntimeException("Issue in creating file directory");
            }
            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Issue in storing the file", e);
            }
            ResourceFileStream fileStream = new ResourceFileStream(file.getOriginalFilename(), null, VIDEO_PATH,
                    author);
            if (fileStreamRepository.save(fileStream) == null) {
                throw new RuntimeException("Sorry :: Something gone wrong :: this File Stream  does'nt Saved !");
            }
            return new ResponseEntity<MessageResponse>(new MessageResponse("Audio uploaded succesfully!"),
                    HttpStatus.OK);

        }
        throw new ForbiddenMimeTypeException("Sorry :: Something gone wrong :: this File is not a Video||Audio");

    }

    // /api/audiovideo/file/{filename}
    @GetMapping(value = "/file/{filename}")
    public ResponseEntity<?> getVideosAudiosByName(@PathVariable("filename") final String filename) {

        final List<ResourceFileStream> files = fileStreamRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException(
                        "Sorry :: Something gone wrong :: this filename {" + filename + "} does'nt exists"));

        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    // /api/audiovideo/all?tags=...
    /*
     * @GetMapping(value = "/all", consumes = MediaType.APPLICATION_JSON_VALUE)
     * public ResponseEntity<?> getVideosAudiosByTags(@RequestParam("tags") final
     * List<String> tags) {
     * 
     * final BasicQuery query = new BasicQuery("{ tags : { $in : " + tags + "}}");
     * final List<ResourceFileStream> files = mongoTemplate.find(query,
     * ResourceFileStream.class);
     * 
     * return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK); }
     */
    // api/audiovideo/search/all?keyword=...
    @GetMapping(value = "/search/all")
    public ResponseEntity<?> getVideosAudiosByFilename(@RequestParam("keyword") final String keyword) {

        List<ResourceFileStream> files = new ArrayList<ResourceFileStream>();

        if (keyword == null) {
            fileStreamRepository.findAll().forEach(files::add);
        } else {
            fileStreamRepository.findByFilenameContaining(keyword).forEach(files::add);
        }

        if (keyword.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        /*
         * final BasicQuery query = new BasicQuery("{ filename : { $regex : " + keyword
         * + " , $options : i}}"); final List<ResourceFileStream> files =
         * mongoTemplate.find(query, ResourceFileStream.class);
         */

        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    // /api/audiovideo/{id}
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResourceFileStream> getVideoAudioById(@PathVariable("id") final String id) {

        final ResourceFileStream file = fileStreamRepository.findById(id)
                .orElseThrow(() -> new FileStreamNotFoundException(
                        "Sorry :: Something gone wrong :: this File Stream Id {" + id + "} does'nt exists"));

        // final BasicQuery query = new BasicQuery("{ _id : { " + id + " }");
        // final ResourceFileStream file = mongoTemplate.findById(id,
        // ResourceFileStream.class);

        return new ResponseEntity<ResourceFileStream>(file, HttpStatus.OK);
    }

    // /api/audiovideo/author?id=...
    @GetMapping("/author")
    public ResponseEntity<?> getVideosAudiosByAuthor(@RequestParam("id") final String id) {

        /*
         * METHOD 1 BasicQuery query = new BasicQuery("{ author : " + id + "}");
         * List<ResourceFileStream> files = mongoTemplate.find(query,
         * ResourceFileStream.class);
         */
        // METHOD 2
        final User author = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                "Sorry :: Something gone wrong :: this User Id {" + id + "} does'nt exists"));

        final List<ResourceFileStream> files = fileStreamRepository.findByAuthor(author)
                .orElseThrow(() -> new RuntimeException(
                        "Sorry :: Something gone wrong : There's no Resource files with this Author Id {" + id + "} "));
        return new ResponseEntity<List<ResourceFileStream>>(files, HttpStatus.OK);
    }

    // /api/audiovideo/download/{id}
    @GetMapping(value = "/download/{id}")
    public ResponseEntity<Resource> downloadResourceFileStream(@PathVariable("id") String resource_id,
            HttpServletRequest request) {

        ResourceFileStream file = fileStreamRepository.findById(resource_id)
                .orElseThrow(() -> new FileStreamNotFoundException(
                        "Sorry :: Something gone wrong :: This Resource file does not exits with ID { " + resource_id
                                + " }"));
        Path path = Paths.get(file.getPath());
        Resource resource;
        String mimeType;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in reading the file", e);
        }

        if (resource.exists() && resource.isReadable()) {
            try {
                mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            mimeType = mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType;

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename())
                    .body(resource);
        } else {
            throw new RuntimeException("Sorry :: Something gone wrong :: The file doesn't exist or not readable");
        }
    }

    @DeleteMapping(value = "/{resource_id}")
    public ResponseEntity<?> deleteResourceById(@PathVariable("resource_id") String resource_id) {
        ResourceFileStream file = fileStreamRepository.findById(resource_id)
                .orElseThrow(() -> new FileStreamNotFoundException(
                        "Sorry :: Something gone wrong :: This Resource file does not exits with ID { " + resource_id
                                + " }"));
        System.out.println("*********");
        try {
            Files.delete(Paths.get(file.getPath()));
        } catch (IOException e) {
            throw new FileStreamNotFoundException(
                    "Sorry :: Something gone wrong :: This Resource file does not exits with ID { " + resource_id
                            + " }");
        }
        fileStreamRepository.deleteById(resource_id);
        return ResponseEntity.ok(new MessageResponse("Resource file was deleted successfully !"));
    }
}