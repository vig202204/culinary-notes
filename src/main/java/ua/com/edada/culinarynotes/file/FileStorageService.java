package ua.com.edada.culinarynotes.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Stores a file in the file system.
     *
     * @param file the file to store
     * @return the name of the stored file
     * @throws IOException if an I/O error occurs
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        
        // Generate unique file name
        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID() + fileExtension;
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        
        // Copy file to the target location
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Stored file: {} (original: {})", fileName, originalFilename);
        return fileName;
    }

    /**
     * Loads a file as a Resource.
     *
     * @param fileName the name of the file to load
     * @return the file as a Resource
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                log.info("Loaded file: {}", fileName);
                return resource;
            } else {
                log.error("File not found: {}", fileName);
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("Error loading file: {}", fileName, ex);
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
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("Deleted file: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }
            
            return deleted;
        } catch (IOException ex) {
            log.error("Error deleting file: {}", fileName, ex);
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