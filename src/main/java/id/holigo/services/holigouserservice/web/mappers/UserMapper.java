package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
