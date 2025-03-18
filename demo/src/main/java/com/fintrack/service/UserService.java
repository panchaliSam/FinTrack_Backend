package com.fintrack.service;

import com.fintrack.type.Role;
import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;
import com.fintrack.utility.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with the same email already exists.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        if (role == Role.REGULAR_USER) {
            notifyAdminsOfNewUser(savedUser);
        }

        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> updateUser(String id, String firstName, String lastName, String email, String password, Role role) {
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
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    public boolean deleteUser(String id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return jwtTokenProvider.generateToken(user.getEmail());
    }

    private void notifyAdminsOfNewUser(User user){
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
        }
    }
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).get();
            }
        };
    }
}
