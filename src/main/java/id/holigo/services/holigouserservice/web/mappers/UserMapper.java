package id.holigo.services.holigouserservice.web.mappers;

import id.holigo.services.common.model.UserParentDto;
import id.holigo.services.holigouserservice.web.model.UserDtoForUser;
import org.mapstruct.Mapper;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    @Mapping(target = "parent.referral", source = "parent.userReferral.referral")
    UserDtoForUser userToUserDtoForUser(User user);

    UserParentDto userToUserParentDto(User user);
}
