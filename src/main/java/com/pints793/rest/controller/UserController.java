package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.IdType;
import com.pints793.Utils;
import com.pints793.organisation.Invitation;
import com.pints793.user.LoginRequest;
import com.pints793.user.LoginResponse;
import com.pints793.user.NewUserRequest;
import com.pints793.user.UpdateProfileRequest;
import com.pints793.user.User;
import com.pints793.user.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserProfileResponse response = new UserProfileResponse()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setBio(user.getBio())
                .setProfilePicture(user.getProfilePicture());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token,
                                        @PathVariable("userId") String userId) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user2 = userCollection.findById(userId).orElse(null);

        if (user2 == null || !usersShareOrganisation(user, user2)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserProfileResponse response = new UserProfileResponse()
                .setUsername(user2.getUsername())
                .setEmail(user2.getEmail())
                .setName(user2.getName())
                .setBio(user2.getBio())
                .setProfilePicture(user2.getProfilePicture());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                           @RequestBody UpdateProfileRequest request) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request.getName() != null) {
            user.setName(request.getName().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }

        userCollection.save(user);

        UserProfileResponse response = new UserProfileResponse()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setBio(user.getBio())
                .setProfilePicture(user.getProfilePicture());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader("Authorization") String token,
                                                  @RequestParam("file") MultipartFile file) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            byte[] bytes = file.getBytes();
            String base64 = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
            user.setProfilePicture(base64);
            userCollection.save(user);

            UserProfileResponse response = new UserProfileResponse()
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setName(user.getName())
                    .setBio(user.getBio())
                    .setProfilePicture(user.getProfilePicture());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile_image/{id}")
    public ResponseEntity<?> getInvitationImage(@RequestHeader("Authorization") String token,
                                                @PathVariable("id") String id) {
        User user = getUser(token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (id.startsWith(IdType.INVITATION.toString())) {
            Invitation invitation = invitationCollection.findById(id).orElse(null);

            if (invitation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (!invitation.getRecipientUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            User sender = userCollection.findById(invitation.getSenderUserId()).orElse(null);

            if (sender == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(sender.getProfilePicture());
        } else if (id.startsWith(IdType.USER.toString())) {
            User user2 = userCollection.findById(id).orElse(null);

            if (user2 == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (usersShareOrganisation(user, user2)) {
                return ResponseEntity.ok(user2.getProfilePicture());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/profile_images")
    public ResponseEntity<?> getProfileImages(@RequestHeader("Authorization") String token,
                                              @RequestBody List<String> userIds) {
        User caller = getUser(token);

        if (caller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> result = new HashMap<>();

        for (String userId : userIds) {
            User target = userCollection.findById(userId).orElse(null);
            if (target == null || !usersShareOrganisation(caller, target)) {
                continue;
            }
            String picture = target.getProfilePicture();
            if (picture != null) {
                String thumbnail = generateThumbnail(picture, 48);
                result.put(userId, thumbnail != null ? thumbnail : picture);
            }
        }

        return ResponseEntity.ok(result);
    }

    private String generateThumbnail(String dataUrl, int size) {
        try {
            // Parse "data:<mime>;base64,<data>"
            int commaIdx = dataUrl.indexOf(',');
            if (commaIdx < 0) return null;

            String header = dataUrl.substring(0, commaIdx); // "data:image/jpeg;base64"
            String base64Data = dataUrl.substring(commaIdx + 1);

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (original == null) return null;

            int origW = original.getWidth();
            int origH = original.getHeight();

            if (origW <= size && origH <= size) {
                return dataUrl; // Already small enough
            }

            // Scale down preserving aspect ratio
            double scale = Math.min((double) size / origW, (double) size / origH);
            int newW = Math.max(1, (int) (origW * scale));
            int newH = Math.max(1, (int) (origH * scale));

            BufferedImage thumbnail = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(original, 0, 0, newW, newH, null);
            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpeg", out);
            String thumbBase64 = Base64.getEncoder().encodeToString(out.toByteArray());

            return "data:image/jpeg;base64," + thumbBase64;
        } catch (Exception e) {
            return null;
        }
    }
}
