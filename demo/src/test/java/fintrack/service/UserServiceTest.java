package fintrack.service;

import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.EmailService;
import com.fintrack.service.UserService;
import com.fintrack.type.Role;
import com.fintrack.utility.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private PasswordEncoder passwordEncoder;
    private User mockUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        // Mock User
        mockUser = new User();
        mockUser.setId("user123");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setPassword(passwordEncoder.encode("password123"));
        mockUser.setRole(Role.REGULAR_USER);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User createdUser = userService.createUser("John", "Doe", "john@example.com", "password123", Role.REGULAR_USER);

        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ExistingEmail_ThrowsException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("John", "Doe", "john@example.com", "password123", Role.REGULAR_USER));

        assertEquals("User with the same email already exists.", exception.getMessage());
    }

    @Test
    void getUserById_Found() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        Optional<User> user = userService.getUserById("user123");

        assertTrue(user.isPresent());
        assertEquals("John", user.get().getFirstName());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById("user999")).thenReturn(Optional.empty());

        Optional<User> user = userService.getUserById("user999");

        assertFalse(user.isPresent());
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void getAllUsers_Empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> users = userService.getAllUsers();

        assertTrue(users.isEmpty());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        Optional<User> updatedUser = userService.updateUser(
                "user123", "John", "Doe", "newemail@example.com", "newpassword", Role.ADMIN);

        assertTrue(updatedUser.isPresent());
        assertEquals("newemail@example.com", updatedUser.get().getEmail());
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById("user999")).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUser(
                "user999", "John", "Doe", "newemail@example.com", "newpassword", Role.ADMIN);

        assertFalse(updatedUser.isPresent());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).deleteById("user123");

        boolean result = userService.deleteUser("user123");

        assertTrue(result);
        verify(userRepository, times(1)).deleteById("user123");
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById("user999")).thenReturn(Optional.empty());

        boolean result = userService.deleteUser("user999");

        assertFalse(result);
    }

    @Test
    void authenticateUser_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.generateToken("john@example.com")).thenReturn("mockToken");

        String token = userService.authenticateUser("john@example.com", "password123");

        assertNotNull(token);
        assertEquals("mockToken", token);
    }

    @Test
    void authenticateUser_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser("john@example.com", "wrongpassword"));

        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    void authenticateUser_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.authenticateUser("nonexistent@example.com", "password123"));

        assertEquals("Invalid email or password.", exception.getMessage());
    }
}