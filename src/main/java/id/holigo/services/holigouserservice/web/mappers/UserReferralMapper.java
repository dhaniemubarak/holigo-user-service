package id.holigo.services.holigouserservice.web.mappers;

import org.mapstruct.Mapper;

import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.web.model.UserReferralDto;

@Mapper
public interface UserReferralMapper {

    UserReferralDto userReferralToUserReferralDto(UserReferral userReferral);

    UserReferral userReferralDtoToUserReferral(UserReferralDto userReferralDto);
}