package id.holigo.services.holigouserservice.services.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import id.holigo.services.holigouserservice.config.FileStorageProperties;
import id.holigo.services.holigouserservice.web.exceptions.FileStorageException;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Could not create the directory where the upload files will be store. ", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // Normalize fileName
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
        return fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        Resource resource;
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new NotFoundException("File not found");
            }
        } catch (Exception e) {
            throw new NotFoundException("File not found " + fileName, e);
        }
        return resource;
    }

    @Override
    public boolean deleteFile(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName);
        // Path targetLocation = this.fileStorageLocation.resolve(fileName);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
