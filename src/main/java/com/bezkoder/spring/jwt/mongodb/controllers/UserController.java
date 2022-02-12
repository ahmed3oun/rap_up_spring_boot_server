package com.bezkoder.spring.jwt.mongodb.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.bezkoder.spring.jwt.mongodb.Exceptions.RoleNotFoundException;
import com.bezkoder.spring.jwt.mongodb.Exceptions.UserNotFoundException;
import com.bezkoder.spring.jwt.mongodb.models.Role;
import com.bezkoder.spring.jwt.mongodb.models.User;
import com.bezkoder.spring.jwt.mongodb.payload.request.UserProfileRequest;
import com.bezkoder.spring.jwt.mongodb.payload.response.MessageResponse;
import com.bezkoder.spring.jwt.mongodb.repository.RoleRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping(value = "/users"/* , consumes = MediaType.APPLICATION_JSON_VALUE */)
    public ResponseEntity<?> getUsersByUsernameContaining(
            @RequestParam(value = "subname", required = false) String subname) {
        List<User> users = new ArrayList<User>();

        if (subname == null) {
            userRepository.findAll().forEach(users::add);
        } else {
            userRepository.findByUsernameContaining(subname).forEach(users::add);
        }

        if (subname != null) {
            if (subname.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{id}"/* , consumes = MediaType.APPLICATION_JSON_VALUE */)
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                "Sorry :: Something gone wrong :: This User with id { " + id + " } does not exist"));

        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value = "/user/{id}"/* , consumes = MediaType.APPLICATION_JSON_VALUE */)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") String id) {
        userRepository.deleteById(id);
        if (userRepository.existsById(id)) {
            return new ResponseEntity<MessageResponse>(
                    new MessageResponse("Sorry :: Something gone wrong :: this User does not deleted! Try again"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<MessageResponse>(new MessageResponse("User deleted successfully !"),
                    HttpStatus.OK);
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUserProfileById(@PathVariable("id") String id,
            @RequestBody UserProfileRequest userProfile) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User _user = user.get();
            if (userRepository.existsByEmail(userProfile.getEmail())) {
                return new ResponseEntity<MessageResponse>(new MessageResponse("Sorry this new mail is already exist"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            _user.setEmail(userProfile.getEmail());
            if (userRepository.existsByUsername(userProfile.getUsername())) {
                return new ResponseEntity<MessageResponse>(
                        new MessageResponse("Sorry this new username is already exist"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            _user.setUsername(userProfile.getUsername());

            _user.setPassword(encoder.encode(userProfile.getPassword()));

            if (userRepository.save(_user) == null)
                throw new RuntimeException("Sorry :: Something gone wrong :: this User does not updated! Try again");

            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        }
        throw new UserNotFoundException(
                "Sorry :: Something gone wrong :: This User with id { " + id + " } does not exist");
    }

    // api/user?user_id=...&role_id=...
    @PatchMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@RequestParam(required = true, value = "user_id") String user_id,
            @RequestParam(value = "role_id", required = true) String role_id) {
        User _user = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(
                "Sorry :: Something gone wrong :: This User with id { " + user_id + " } does not exist"));

        final Role _role = roleRepository.findById(role_id).orElseThrow(() -> new RoleNotFoundException(
                "Sorry :: Something gone wrong :: This Role with id { " + role_id + " } does not exist"));

        final Set<Role> roles = new HashSet<>();
        roles.add(_role);

        _user.setRoles(roles);

        if (userRepository.save(_user) == null)
            throw new RuntimeException("Sorry :: Something gone wrong :: this User role does not updated! Try again");

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));

    }

}