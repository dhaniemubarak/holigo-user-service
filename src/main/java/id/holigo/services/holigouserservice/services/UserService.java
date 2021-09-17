package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.UserDto;

public interface UserService {
    UserDto findById(Long id);

    UserDto save(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    boolean isEmailAlreadyInUse(String email);

    boolean isPhoneNumberAlreadyInUse(String phoneNumber);
}
