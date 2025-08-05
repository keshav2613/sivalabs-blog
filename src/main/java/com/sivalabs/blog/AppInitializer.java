package com.sivalabs.blog;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class AppInitializer implements CommandLineRunner {
    private final ApplicationProperties properties;

    public AppInitializer(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(String... args) throws Exception {
        var fileStorageLocation =
                Paths.get(properties.fileUploadsDir()).toAbsolutePath().normalize();
        Files.createDirectories(fileStorageLocation);
    }
}
