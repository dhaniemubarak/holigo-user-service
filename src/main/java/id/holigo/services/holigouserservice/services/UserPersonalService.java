package id.holigo.services.holigouserservice.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfilDto;

public interface UserPersonalService {
    UserPersonalDto getUserPersonalByUserId(Long userId);

    UserPersonal createUserPersona(UserPersonal userPersonal) throws Exception;

    UserPersonalDto createUserPersonalByUserId(Long userId, UserPersonalDto userPersonalDto) throws Exception;

    UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto);

    UserPersonalPhotoProfilDto savePhotoProfile(Long personalId, MultipartFile file) throws Exception;

    Resource getPhotoProfile(String fileName);

    boolean deletePhotoProfile(Long photoProfileId);
}
