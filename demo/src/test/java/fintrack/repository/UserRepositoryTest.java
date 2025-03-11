package fintrack.repository;

import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;
import com.fintrack.type.Role;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;
    private String userId;
    private String email;

    @BeforeEach
    void setUp() {
        userId = new ObjectId().toString();
        email = "testuser@example.com";

        user = new User();
        user.setId(String.valueOf(new ObjectId(userId)));
        user.setEmail(email);
        user.setPassword("securepassword");
        user.setRole(Role.REGULAR_USER);
    }

    @Test
    void findByEmail_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> retrievedUser = userRepository.findByEmail(email);

        assertTrue(retrievedUser.isPresent());
        assertEquals(userId, retrievedUser.get().getId().toString());
        assertEquals(email, retrievedUser.get().getEmail());
        assertEquals("securepassword", retrievedUser.get().getPassword());
        assertEquals(Role.REGULAR_USER, retrievedUser.get().getRole());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_NotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> retrievedUser = userRepository.findByEmail(email);

        assertFalse(retrievedUser.isPresent());

        verify(userRepository, times(1)).findByEmail(email);
    }
}