package id.holigo.services.holigouserservice.services.storage;

import id.holigo.services.holigouserservice.config.FileStorageProperties;
import id.holigo.services.holigouserservice.web.exceptions.FileStorageException;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.model.ImageKitDto;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
    public ImageKitDto storeFile(MultipartFile file, Long personalId) {
        // Normalize fileName
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        ImageKitDto imageKitDto = new ImageKitDto();
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            FileCreateRequest fileCreateRequest = new FileCreateRequest(file.getBytes(), personalId+"_"+fileName);

            fileCreateRequest.setFolder("user_profile_photo");

            Result result = ImageKit.getInstance().upload(fileCreateRequest);
            imageKitDto.setFileId(result.getFileId());
            imageKitDto.setFileName(result.getName());
            imageKitDto.setFileType(result.getFileType());
            imageKitDto.setUrl(result.getUrl());

//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
        return imageKitDto;
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
    public boolean deleteFile(String fileId) {
        try {
            Result result = ImageKit.getInstance().deleteFile(fileId);

            return result.isSuccessful();

        } catch (Exception e) {
            throw new FileStorageException("File failed to delete, Please try again!", e);
        }
    }

}
