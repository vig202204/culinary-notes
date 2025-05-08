package ua.com.edada.culinarynotes.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Stores a file in the file system.
     *
     * @param file the file to store
     * @return the name of the stored file
     * @throws IOException if an I/O error occurs
     */
    public String storeFile(MultipartFile file) throws IOException {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "storeFile");

        try {
            // Normalize file name
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            MDC.put("originalFilename", originalFilename);

            // Generate unique file name
            String fileExtension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID() + fileExtension;
            MDC.put("fileName", fileName);

            if (log.isDebugEnabled()) {
                log.debug("Storing file: {} (original: {}). OperationId: {}, Time: {}", 
                        fileName, originalFilename, operationId, LocalDateTime.now().format(FORMATTER));
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Copy file to the target location
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            if (log.isInfoEnabled()) {
                log.info("Stored file: {} (original: {}). OperationId: {}, Time: {}", 
                        fileName, originalFilename, operationId, LocalDateTime.now().format(FORMATTER));
            }

            MDC.clear();
            return fileName;
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Error storing file. OperationId: {}, Time: {}", 
                        operationId, LocalDateTime.now().format(FORMATTER), ex);
            }
            MDC.clear();
            throw ex;
        }
    }

    /**
     * Loads a file as a Resource.
     *
     * @param fileName the name of the file to load
     * @return the file as a Resource
     */
    public Resource loadFileAsResource(String fileName) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("fileName", fileName);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "loadFileAsResource");

        if (log.isDebugEnabled()) {
            log.debug("Loading file: {}. OperationId: {}, Time: {}", 
                    fileName, operationId, LocalDateTime.now().format(FORMATTER));
        }

        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                if (log.isInfoEnabled()) {
                    log.info("Loaded file: {}. OperationId: {}, Time: {}", 
                            fileName, operationId, LocalDateTime.now().format(FORMATTER));
                }
                MDC.clear();
                return resource;
            } else {
                if (log.isErrorEnabled()) {
                    log.error("File not found: {}. OperationId: {}, Time: {}", 
                            fileName, operationId, LocalDateTime.now().format(FORMATTER));
                }
                MDC.clear();
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            if (log.isErrorEnabled()) {
                log.error("Error loading file: {}. OperationId: {}, Time: {}", 
                        fileName, operationId, LocalDateTime.now().format(FORMATTER), ex);
            }
            MDC.clear();
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    /**
     * Deletes a file from the file system.
     *
     * @param fileName the name of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public boolean deleteFile(String fileName) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("fileName", fileName);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "deleteFile");

        if (log.isDebugEnabled()) {
            log.debug("Deleting file: {}. OperationId: {}, Time: {}", 
                    fileName, operationId, LocalDateTime.now().format(FORMATTER));
        }

        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                if (log.isInfoEnabled()) {
                    log.info("Deleted file: {}. OperationId: {}, Time: {}", 
                            fileName, operationId, LocalDateTime.now().format(FORMATTER));
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("File not found for deletion: {}. OperationId: {}, Time: {}", 
                            fileName, operationId, LocalDateTime.now().format(FORMATTER));
                }
            }

            MDC.clear();
            return deleted;
        } catch (IOException ex) {
            if (log.isErrorEnabled()) {
                log.error("Error deleting file: {}. OperationId: {}, Time: {}", 
                        fileName, operationId, LocalDateTime.now().format(FORMATTER), ex);
            }
            MDC.clear();
            return false;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }
}
