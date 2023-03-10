package id.holigo.services.holigouserservice.services;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.web.model.DeletedUserDto;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;
import id.holigo.services.holigouserservice.web.requests.ResetPin;

public interface UserService {
    UserDto findById(Long id);

    User save(UserDto userDto) throws Exception;

    UserDto update(Long id, UserDto userDto);

    void deleteUser(User user, DeletedUserDto deletedUserDto);

    boolean isEmailAlreadyInUse(String email);

    boolean isPhoneNumberAlreadyInUse(String phoneNumber);

    UserDto getByPhoneNumber(String phoneNumber);

    UserPaginate getAllUser();

    void createNewPin(Long userId, CreateNewPin createNewPin) throws Exception;

    void resetPin(Long userId, ResetPin resetPin) throws Exception;

    void updatePin(Long userId, ChangePin changePin) throws Exception;

//    void addAuthorityToUser(String phoneNumber, String role);

    void createOneTimePassword(User user, String oneTimePassword);

    void resetOneTimePassword(User user);

    UserDto fetchReferral(UserDto userDto);

}
