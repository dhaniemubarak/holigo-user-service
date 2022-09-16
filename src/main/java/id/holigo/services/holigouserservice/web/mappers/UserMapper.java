package id.holigo.services.holigouserservice.web.mappers;

import id.holigo.services.common.model.UserParentDto;
import id.holigo.services.holigouserservice.web.model.UserDtoForUser;
import org.mapstruct.Mapper;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Mapping(target = "registerId", ignore = true)
    UserDto userToUserDto(User user);

    @Mapping(target = "userReferral", ignore = true)
    @Mapping(target = "userPersonal", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pin", ignore = true)
    @Mapping(target = "oneTimePassword", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authority", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    User userDtoToUser(UserDto userDto);

    @Mapping(target = "parent.referral", source = "parent.userReferral.referral")
    UserDtoForUser userToUserDtoForUser(User user);

    UserParentDto userToUserParentDto(User user);
}
