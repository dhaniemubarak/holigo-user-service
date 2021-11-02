package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserPersonalPhotoProfil;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfilDto;

@Mapper
public interface UserPersonalPhotoProfileMapper {
    UserPersonalPhotoProfil userPersonalPhotoProfileDtoToUserPersonalPhotoProfile(
            UserPersonalPhotoProfilDto userPersonalPhotoProfilDto);

    UserPersonalPhotoProfilDto userPersonalPhotoProfileToUserPersonalPhotoProfileDto(
            UserPersonalPhotoProfil userPersonalPhotoProfil);
}
