package id.holigo.services.holigouserservice.services;

import id.holigo.services.holigouserservice.web.model.UserPersonalDto;

public interface UserPersonalService {
    UserPersonalDto getUserPersonalByUserId(Long userId);

    UserPersonalDto createUserPersonal(Long userId, UserPersonalDto userPersonalDto);

    UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto);
}
