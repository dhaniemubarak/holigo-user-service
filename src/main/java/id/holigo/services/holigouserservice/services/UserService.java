package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.model.UserRegisterDto;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;

public interface UserService {
    UserDto findById(Long id);

    UserDto save(UserDto userDto);

    UserRegisterDto createUserViaOtp(UserRegisterDto userRegisterDto);

    UserDto update(Long id, UserDto userDto);

    boolean isEmailAlreadyInUse(String email);

    boolean isPhoneNumberAlreadyInUse(String phoneNumber);

    UserDto getByPhoneNumber(String phoneNumber);

    UserPaginate getAllUser();

    UserDto createNewPin(Long userId, CreateNewPin createNewPin)  throws Exception;

    UserDto updatePin(Long userId, ChangePin changePin) throws Exception;
}
