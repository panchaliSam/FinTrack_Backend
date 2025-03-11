package fintrack.controller;

import com.fintrack.controller.UserController;
import com.fintrack.dto.UserDto;
import com.fintrack.entity.User;
import com.fintrack.service.UserService;
import com.fintrack.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        mockUser = new User("12345", "John", "Doe", "john@example.com", "password123", Role.REGULAR_USER);
        mockUserDto = new UserDto("John", "Doe", "john@example.com", "password123", Role.REGULAR_USER);
    }

    @Test
    void registerUser() {
        when(userService.createUser(
                mockUserDto.getFirstName(),
                mockUserDto.getLastName(),
                mockUserDto.getEmail(),
                mockUserDto.getPassword(),
                mockUserDto.getRole()
        )).thenReturn(mockUser);

        ResponseEntity<String> response = userController.registerUser(mockUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());

        verify(userService, times(1)).createUser(
                mockUserDto.getFirstName(),
                mockUserDto.getLastName(),
                mockUserDto.getEmail(),
                mockUserDto.getPassword(),
                mockUserDto.getRole()
        );
    }

    @Test
    void signIn_Success() {
        String token = "mockToken";
        when(userService.authenticateUser("john@example.com", "password123")).thenReturn(token);

        ResponseEntity<Map<String, String>> response = userController.signIn(Map.of(
                "email", "john@example.com",
                "password", "password123"
        ));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(token, response.getBody().get("token"));
        verify(userService, times(1)).authenticateUser("john@example.com", "password123");
    }

    @Test
    void signIn_Failure() {
        when(userService.authenticateUser("john@example.com", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        ResponseEntity<Map<String, String>> response = userController.signIn(Map.of(
                "email", "john@example.com",
                "password", "wrongpassword"
        ));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody().get("error"));
    }

    @Test
    void getAllUsers_Success() {
        when(userService.getAllUsers()).thenReturn(List.of(mockUser));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_Empty() {
        when(userService.getAllUsers()).thenReturn(List.of());

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getUserById_Found() {
        when(userService.getUserById("12345")).thenReturn(Optional.of(mockUser));

        ResponseEntity<User> response = userController.getUserById("12345");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void getUserById_NotFound() {
        when(userService.getUserById("99999")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("99999");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updateUser_Success() {
        when(userService.updateUser(
                "12345",
                mockUserDto.getFirstName(),
                mockUserDto.getLastName(),
                mockUserDto.getEmail(),
                mockUserDto.getPassword(),
                mockUserDto.getRole()
        )).thenReturn(Optional.of(mockUser));

        ResponseEntity<String> response = userController.updateUser("12345", mockUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User updated successfully", response.getBody());
    }

    @Test
    void updateUser_NotFound() {
        when(userService.updateUser(
                "99999",
                mockUserDto.getFirstName(),
                mockUserDto.getLastName(),
                mockUserDto.getEmail(),
                mockUserDto.getPassword(),
                mockUserDto.getRole()
        )).thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.updateUser("99999", mockUserDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteUser_Success() {
        when(userService.deleteUser("12345")).thenReturn(true);

        ResponseEntity<String> response = userController.deleteUser("12345");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
    }

    @Test
    void deleteUser_NotFound() {
        when(userService.deleteUser("99999")).thenReturn(false);

        ResponseEntity<String> response = userController.deleteUser("99999");

        assertEquals(404, response.getStatusCodeValue());
    }
}