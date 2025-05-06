package ua.com.edada.culinarynotes.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = User.builder()
                .id(1L)
                .username("johndoe")
                .password("password123")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .bio("I love cooking")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .id(2L)
                .username("janedoe")
                .password("password456")
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .bio("I love baking")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users).contains(user1, user2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertThat(result).isEqualTo(user1);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: '999'");
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByUsername_WithExistingUsername_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user1));

        // Act
        Optional<User> result = userService.getUserByUsername("johndoe");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user1);
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void getUserByUsername_WithNonExistingUsername_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByUsername("nonexistent");

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void getUserByEmail_WithExistingEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user1));

        // Act
        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user1);
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void getUserByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void createUser_WithUniqueUsernameAndEmail_ShouldCreateUser() {
        // Arrange
        User newUser = User.builder()
                .username("newuser")
                .password("password789")
                .email("new.user@example.com")
                .firstName("New")
                .lastName("User")
                .build();
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new.user@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User createdUser = userService.createUser(newUser);

        // Assert
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("newuser");
        assertThat(createdUser.getEmail()).isEqualTo("new.user@example.com");
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("new.user@example.com");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        User newUser = User.builder()
                .username("johndoe")
                .password("password789")
                .email("new.user@example.com")
                .build();
        
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
        verify(userRepository, times(1)).existsByUsername("johndoe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        User newUser = User.builder()
                .username("newuser")
                .password("password789")
                .email("john.doe@example.com")
                .build();
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidChanges_ShouldUpdateUser() {
        // Arrange
        User userDetails = User.builder()
                .username("johndoe_updated")
                .email("john.updated@example.com")
                .firstName("John Updated")
                .lastName("Doe Updated")
                .bio("Updated bio")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("johndoe_updated")).thenReturn(false);
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        
        User updatedUser = User.builder()
                .id(1L)
                .username("johndoe_updated")
                .password("password123")
                .email("john.updated@example.com")
                .firstName("John Updated")
                .lastName("Doe Updated")
                .bio("Updated bio")
                .createdAt(user1.getCreatedAt())
                .updatedAt(user1.getUpdatedAt())
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(1L, userDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe_updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getFirstName()).isEqualTo("John Updated");
        assertThat(result.getLastName()).isEqualTo("Doe Updated");
        assertThat(result.getBio()).isEqualTo("Updated bio");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByUsername("johndoe_updated");
        verify(userRepository, times(1)).existsByEmail("john.updated@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        User userDetails = User.builder()
                .username("janedoe")  // This username belongs to user2
                .email("john.updated@example.com")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("janedoe")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(1L, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByUsername("janedoe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        User userDetails = User.builder()
                .username("johndoe")
                .email("jane.doe@example.com")  // This email belongs to user2
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(1L, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, times(1)).existsByEmail("jane.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithExistingId_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: '999'");
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}