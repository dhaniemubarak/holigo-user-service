package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;
import id.holigo.services.holigouserservice.web.requests.ResetPin;

public interface UserService {
    UserDto findById(Long id);

    User save(UserDto userDto) throws Exception;

    UserDto update(Long id, UserDto userDto);

    boolean isEmailAlreadyInUse(String email);

    boolean isPhoneNumberAlreadyInUse(String phoneNumber);

    UserDto getByPhoneNumber(String phoneNumber);

    UserPaginate getAllUser();

    UserDto createNewPin(Long userId, CreateNewPin createNewPin) throws Exception;

    UserDto resetPin(Long userId, ResetPin resetPin) throws Exception;

    UserDto updatePin(Long userId, ChangePin changePin) throws Exception;

    void addAuthorityToUser(String phoneNumber, String role);

    void createOneTimePassword(User user, String oneTimePassword);

    void resetOneTimePassword(User user);

    UserDto fetchReferral(UserDto userDto);

}
