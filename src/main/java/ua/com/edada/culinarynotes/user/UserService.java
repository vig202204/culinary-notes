package ua.com.edada.culinarynotes.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getAllUsers");

        if (log.isDebugEnabled()) {
            log.debug("Getting all users. OperationId: {}, Time: {}", 
                    operationId, LocalDateTime.now().format(FORMATTER));
        }

        List<User> users = userRepository.findAll();

        if (log.isDebugEnabled()) {
            log.debug("Found {} users. OperationId: {}", 
                    users.size(), operationId);
        }

        MDC.clear();
        return users;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("userId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getUserById");

        if (log.isDebugEnabled()) {
            log.debug("Getting user with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User not found with id: {}. OperationId: {}", 
                                id, operationId);
                    }
                    MDC.clear();
                    return new ResourceNotFoundException("User", "id", id);
                });

        if (log.isDebugEnabled()) {
            log.debug("User found: {}. OperationId: {}", 
                    user.getUsername(), operationId);
        }

        MDC.clear();
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("username", username);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getUserByUsername");

        if (log.isDebugEnabled()) {
            log.debug("Getting user with username: {}. OperationId: {}, Time: {}", 
                    username, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Optional<User> user = userRepository.findByUsername(username);

        if (log.isDebugEnabled()) {
            log.debug("User found: {}. OperationId: {}", 
                    user.isPresent(), operationId);
        }

        MDC.clear();
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("email", email);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getUserByEmail");

        if (log.isDebugEnabled()) {
            log.debug("Getting user with email: {}. OperationId: {}, Time: {}", 
                    email, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (log.isDebugEnabled()) {
            log.debug("User found: {}. OperationId: {}", 
                    user.isPresent(), operationId);
        }

        MDC.clear();
        return user;
    }

    @Transactional
    public User createUser(User user) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("username", user.getUsername());
        MDC.put("email", user.getEmail());
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "createUser");

        if (log.isDebugEnabled()) {
            log.debug("Creating new user: {}. OperationId: {}, Time: {}", 
                    user.getUsername(), operationId, LocalDateTime.now().format(FORMATTER));
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            if (log.isErrorEnabled()) {
                log.error("Username already exists: {}. OperationId: {}", 
                        user.getUsername(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            if (log.isErrorEnabled()) {
                log.error("Email already exists: {}. OperationId: {}", 
                        user.getEmail(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Email already exists");
        }

        User savedUser = userRepository.save(user);

        if (log.isDebugEnabled()) {
            log.debug("User created with id: {}. OperationId: {}", 
                    savedUser.getId(), operationId);
        }

        MDC.clear();
        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("userId", String.valueOf(id));
        MDC.put("username", userDetails.getUsername());
        MDC.put("email", userDetails.getEmail());
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "updateUser");

        if (log.isDebugEnabled()) {
            log.debug("Updating user with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        User user = getUserById(id);

        if (!user.getUsername().equals(userDetails.getUsername()) && 
                userRepository.existsByUsername(userDetails.getUsername())) {
            if (log.isErrorEnabled()) {
                log.error("Username already exists: {}. OperationId: {}", 
                        userDetails.getUsername(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Username already exists");
        }

        if (!user.getEmail().equals(userDetails.getEmail()) && 
                userRepository.existsByEmail(userDetails.getEmail())) {
            if (log.isErrorEnabled()) {
                log.error("Email already exists: {}. OperationId: {}", 
                        userDetails.getEmail(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Email already exists");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setBio(userDetails.getBio());

        User updatedUser = userRepository.save(user);

        if (log.isDebugEnabled()) {
            log.debug("User updated: {}. OperationId: {}", 
                    updatedUser.getUsername(), operationId);
        }

        MDC.clear();
        return updatedUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("userId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "deleteUser");

        if (log.isDebugEnabled()) {
            log.debug("Deleting user with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        if (!userRepository.existsById(id)) {
            if (log.isErrorEnabled()) {
                log.error("User not found with id: {}. OperationId: {}", 
                        id, operationId);
            }
            MDC.clear();
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);

        if (log.isDebugEnabled()) {
            log.debug("User deleted. OperationId: {}", operationId);
        }

        MDC.clear();
    }
}
