package com.cyril.quiz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationService implements IAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final AppConfig config = AppConfig.getInstance();
    private final String USERS_FILE = config.getUsersFile();
    private final Map<String, UserCredential> userCredentials;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AuthenticationService() {
        this.userCredentials = loadUsers();
    }

    private Map<String, UserCredential> loadUsers() {
        // Attempt to read JSON; support legacy formats and migrate
        try {
            String json = Files.exists(Paths.get(USERS_FILE)) ? Files.readString(Paths.get(USERS_FILE)) : null;
            if (json == null || json.isEmpty()) {
                return new HashMap<>();
            }

            try {
                Type credType = new TypeToken<HashMap<String, UserCredential>>() {}.getType();
                Map<String, UserCredential> users = gson.fromJson(json, credType);
                return users != null ? users : new HashMap<>();
            } catch (Exception parseCredEx) {
                // Fallback: legacy map of username->plaintext (or legacy unhashed)
                Type legacyType = new TypeToken<HashMap<String, String>>() {}.getType();
                Map<String, String> legacy = gson.fromJson(json, legacyType);
                Map<String, UserCredential> migrated = new HashMap<>();
                if (legacy != null) {
                    for (Map.Entry<String, String> e : legacy.entrySet()) {
                        String user = e.getKey();
                        String pwd = e.getValue();
                        if (user != null && pwd != null) {
                            String bcrypt = hashPasswordBcrypt(pwd);
                            migrated.put(user, new UserCredential(bcrypt, null));
                        }
                    }
                    // Persist migrated format
                    saveUsers(migrated);
                    return migrated;
                }
                return new HashMap<>();
            }
        } catch (IOException e) {
            logger.info("Users file not found or unreadable, starting new store: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveUsers(Map<String, UserCredential> users) {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            logger.error("Failed to save users file: {}", e.getMessage(), e);
        }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(this.userCredentials, writer);
        } catch (IOException e) {
            logger.error("Failed to save users file: {}", e.getMessage(), e);
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return false;
        }
        
        UserCredential credential = userCredentials.get(username.trim());
        if (credential == null) {
            return false;
        }
        // If stored hash is BCrypt, verify with BCrypt
        if (credential.hashedPassword != null && isBcryptHash(credential.hashedPassword)) {
            boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(password, credential.hashedPassword);
            return ok;
        }
        // Legacy SHA-256 + salt verification; if matches, migrate to BCrypt
        boolean legacyOk = verifyPasswordLegacy(password, credential.hashedPassword, credential.salt);
        if (legacyOk) {
            String bcrypt = hashPasswordBcrypt(password);
            userCredentials.put(username.trim(), new UserCredential(bcrypt, null));
            saveUsers();
        }
        return legacyOk;
    }

    public boolean signup(String username, String password) {
        if (username == null || password == null || 
            username.trim().isEmpty() || password.length() < config.getPasswordMinLength()) {
            return false;
        }
        
        String cleanUsername = username.trim();
        if (userCredentials.containsKey(cleanUsername)) {
            return false; // Username already exists
        }
        // BCrypt hashing for new accounts
        String hashedPassword = hashPasswordBcrypt(password);
        userCredentials.put(cleanUsername, new UserCredential(hashedPassword, null));
        saveUsers(); // Save the updated user map to the file
        return true;
    }
    
    
    private String hashPasswordLegacy(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    private boolean verifyPasswordLegacy(String password, String hashedPassword, String salt) {
        if (hashedPassword == null || salt == null) return false;
        String hashedInput = hashPasswordLegacy(password, salt);
        return hashedPassword.equals(hashedInput);
    }

    private boolean isBcryptHash(String hash) {
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }

    private String hashPasswordBcrypt(String password) {
        // 12 rounds is a reasonable default; adjust via config if desired
        String salt = org.mindrot.jbcrypt.BCrypt.gensalt(12);
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, salt);
    }
    
    private static class UserCredential {
        private final String hashedPassword;
        private final String salt; // null for BCrypt records

        public UserCredential(String hashedPassword, String salt) {
            this.hashedPassword = hashedPassword;
            this.salt = salt;
        }
    }
}
