package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.IdType;
import com.pints793.Utils;
import com.pints793.user.LoginRequest;
import com.pints793.user.LoginResponse;
import com.pints793.user.NewUserRequest;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController extends ApplicationSupport {

    @PostMapping("/new")
    public ResponseEntity<?> createNewUser(@RequestBody NewUserRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!password.matches(PASSWORD_REGEX)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!username.matches(USERNAME_REGEX)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!email.matches(EMAIL_REGEX)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!userCollection.findByEmail(email).isEmpty() || !userCollection.findByUsername(username).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = new User()
                .setId(Utils.newId(IdType.USER))
                .setUsername(username)
                .setPassword(Utils.encodePassword(password))
                .setEmail(request.getEmail());

        userCollection.save(user);

        String token = Utils.generateToken(user.getId());
        LoginResponse response = new LoginResponse().setToken(token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String identifier = request.getIdentifier();
        String password = request.getPassword();
        List<User> matchedUsers;

        if (identifier.matches(EMAIL_REGEX)) {
            matchedUsers = userCollection.findByEmail(identifier);
        } else if (identifier.matches(USERNAME_REGEX)) {
            matchedUsers = userCollection.findByUsername(identifier);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (matchedUsers == null || matchedUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // no user matched
        }
        if (matchedUsers.size() > 1) {
            // FATAL: multiple users matched
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        User user = matchedUsers.getFirst();

        if (!Utils.passwordMatches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = Utils.generateToken(user.getId());
        LoginResponse response = new LoginResponse().setToken(token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify_token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        return getUser(token) == null
                ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                : ResponseEntity.ok().build();
    }

    @GetMapping("/invitations")
    public ResponseEntity<?> getInvitations(@RequestHeader("Authorization") String token) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(invitationCollection.findByRecipientUserId(user.getId()));
    }
}
