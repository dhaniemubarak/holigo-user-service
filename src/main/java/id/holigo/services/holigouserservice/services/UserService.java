package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.model.UserRegisterDto;

public interface UserService {
    UserDto findById(Long id);

    UserDto save(UserDto userDto);

    UserRegisterDto createUserViaOtp(UserRegisterDto userRegisterDto);

    UserDto update(Long id, UserDto userDto);

    boolean isEmailAlreadyInUse(String email);

    boolean isPhoneNumberAlreadyInUse(String phoneNumber);

    UserDto getByPhoneNumber(String phoneNumber);

    UserPaginate getAllUser();
}
