package com.bezkoder.spring.jwt.mongodb.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.jwt.mongodb.Exceptions.RoleNotFoundException;
import com.bezkoder.spring.jwt.mongodb.Exceptions.TokenRefreshException;
import com.bezkoder.spring.jwt.mongodb.models.ERole;
import com.bezkoder.spring.jwt.mongodb.models.RefreshToken;
import com.bezkoder.spring.jwt.mongodb.models.Role;
import com.bezkoder.spring.jwt.mongodb.models.User;
import com.bezkoder.spring.jwt.mongodb.payload.request.LogOutResquest;
import com.bezkoder.spring.jwt.mongodb.payload.request.LoginRequest;
import com.bezkoder.spring.jwt.mongodb.payload.request.SignupRequest;
import com.bezkoder.spring.jwt.mongodb.payload.request.TokenRefreshRequest;
import com.bezkoder.spring.jwt.mongodb.payload.response.JwtResponse;
import com.bezkoder.spring.jwt.mongodb.payload.response.MessageResponse;
import com.bezkoder.spring.jwt.mongodb.payload.response.TokenRefreshResponse;
import com.bezkoder.spring.jwt.mongodb.repository.RoleRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;
import com.bezkoder.spring.jwt.mongodb.security.jwt.JwtUtils;
import com.bezkoder.spring.jwt.mongodb.security.services.RefreshTokenService;
import com.bezkoder.spring.jwt.mongodb.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RefreshTokenService resfreshTokenService;

	@PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody final LoginRequest loginRequest) {
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String jwt = jwtUtils.generateJwtToken(authentication);

		final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		final List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		final RefreshToken refreshToken = this.resfreshTokenService.creatRefreshToken(userDetails.getId());
		return ResponseEntity
				.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(),
						userDetails.getEmail(), userDetails.getGender(), userDetails.getDateOfBirth(), roles));

	}

	@PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@Valid @RequestBody final SignupRequest signUpRequest) {

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		final User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getGender(), signUpRequest.getDateOfBirth());

		final Set<String> strRoles = signUpRequest.getRoles();
		final Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			final Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
					() -> new RoleNotFoundException("Error : Sorry something gone wrong :: ROLE_USER is not found "));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "ROLE_ADMIN":
						final Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RoleNotFoundException(
										"Error : Sorry something gone wrong :: ROLE_ADMIN is not found "));
						roles.add(adminRole);
						break;

					default:
						final Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RoleNotFoundException(
										"Error : Sorry something gone wrong :: ROLE_USER is not found "));
						roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest refreshRequest) {
		String requestRefreshToken = refreshRequest.getRefreshToken();

		return this.resfreshTokenService.findByToken(requestRefreshToken).map(resfreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				}).orElseThrow(
						() -> new TokenRefreshException("Refresh Token is not in Database !", requestRefreshToken));
	}

	@PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutResquest logOutRequest) {
		this.resfreshTokenService.deleteByUserId(logOutRequest.getUserId());
		return ResponseEntity.ok(new MessageResponse("Log out successful!"));
	}

}
