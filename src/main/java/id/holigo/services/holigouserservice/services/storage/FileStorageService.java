package id.holigo.services.holigouserservice.services.storage;

import id.holigo.services.holigouserservice.web.model.ImageKitDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    ImageKitDto storeFile(MultipartFile file, Long personalId);

//    Resource loadFileAsResource(String fileName);

    boolean deleteFile(String fileId);

}
