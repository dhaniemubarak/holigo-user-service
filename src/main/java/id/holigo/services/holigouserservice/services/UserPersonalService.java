package id.holigo.services.holigouserservice.services;

import id.holigo.services.holigouserservice.web.model.UserPersonalDto;

public interface UserPersonalService {
    UserPersonalDto getUserPersonalByUserId(Long userId);

    UserPersonalDto createUserPersonalByUserId(Long userId, UserPersonalDto userPersonalDto) throws Exception;

    UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto);
}
