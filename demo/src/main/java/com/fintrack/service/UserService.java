package com.fintrack.service;

import com.fintrack.type.Role;
import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;
import com.fintrack.utility.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    public User createUser(String firstName, String lastName, String email, String password, Role role) {
        log.info("Attempting to create user: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            log.error("User with email {} already exists.", email);
            throw new IllegalArgumentException("User with the same email already exists.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        try {
            User savedUser = userRepository.save(user);
            log.info("User created successfully: {}", email);

            if (role == Role.REGULAR_USER) {
                notifyAdminsOfNewUser(savedUser);
            }

            return savedUser;
        } catch (Exception e) {
            log.error("Error while saving user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user.");
        }
    }

    public Optional<User> getUserById(String id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users.");
        return userRepository.findAll();
    }

    public Optional<User> updateUser(String id, String firstName, String lastName, String email, String password, Role role) {
        log.info("Attempting to update user with ID: {}", id);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setRole(role);
            try {
                return Optional.of(userRepository.save(user));
            } catch (Exception e) {
                log.error("Error while updating user: {}", e.getMessage());
                throw new RuntimeException("Failed to update user.");
            }
        } else {
            log.error("User with ID {} not found.", id);
            return Optional.empty();
        }
    }

    public boolean deleteUser(String id) {
        log.info("Attempting to delete user with ID: {}", id);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.deleteById(id);
            log.info("User with ID {} deleted successfully.", id);
            return true;
        } else {
            log.error("User with ID {} not found.", id);
            return false;
        }
    }

    public String authenticateUser(String email, String password) {
        log.info("Attempting to authenticate user with email: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.error("Invalid email or password for email: {}", email);
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Invalid email or password for email: {}", email);
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        log.info("User authenticated successfully, token generated.");
        return token;
    }

    private void notifyAdminsOfNewUser(User user) {
        log.info("Notifying admins of new user: {}", user.getEmail());

        List<String> adminEmails = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (!adminEmails.isEmpty()) {
            String subject = "New User Registration Notification";
            String body = "<p>A new user has registered on FinTrack.</p>" +
                    "<p><b>Name:</b> " + user.getFirstName() + " " + user.getLastName() + "</p>" +
                    "<p><b>Email:</b> " + user.getEmail() + "</p>" +
                    "<p><b>Role:</b> " + user.getRole() + "</p>";

            emailService.sendEmailToMultipleRecipients(adminEmails, subject, body);
            log.info("Admins notified successfully.");
        }
    }

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.info("Loading user by username: {}", username);
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }
}
