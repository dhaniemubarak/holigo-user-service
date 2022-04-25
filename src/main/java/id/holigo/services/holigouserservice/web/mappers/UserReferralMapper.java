package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.web.model.UserReferralDto;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@DecoratedWith(UserReferralMapperDecorator.class)
public interface UserReferralMapper {

    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "isOfficialAccount", source = "user.isOfficialAccount")
    UserReferralDto userReferralToUserReferralDto(UserReferral userReferral);

    UserReferral userReferralDtoToUserReferral(UserReferralDto userReferralDto);
}
