package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserPersonalPhotoProfile;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfileDto;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserPersonalPhotoProfileMapper {
    UserPersonalPhotoProfile userPersonalPhotoProfileDtoToUserPersonalPhotoProfile(
            UserPersonalPhotoProfileDto userPersonalPhotoProfilDto);

    UserPersonalPhotoProfileDto userPersonalPhotoProfileToUserPersonalPhotoProfileDto(
            UserPersonalPhotoProfile userPersonalPhotoProfil);
}
