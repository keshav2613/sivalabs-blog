package com.sivalabs.blog.admin.media;

import com.sivalabs.blog.ApplicationProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    public static final String PATH_SEPARATOR = "/";
    private final Path fileStorageLocation;

    public FileStorageService(ApplicationProperties properties) {
        this.fileStorageLocation =
                Paths.get(properties.fileUploadsDir()).toAbsolutePath().normalize();
    }

    public String storeFile(MultipartFile file, String dir) throws IOException {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Original filename is null");
        String fileName = StringUtils.cleanPath(originalFilename);
        if (fileName.contains("..")) {
            throw new IOException("Invalid file path: " + fileName);
        }
        Files.createDirectories(Path.of(this.fileStorageLocation + PATH_SEPARATOR + dir));
        String filepath = dir + PATH_SEPARATOR + UUID.randomUUID() + "-" + fileName;
        Path targetLocation = this.fileStorageLocation.resolve(filepath);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return PATH_SEPARATOR + filepath;
    }
}
