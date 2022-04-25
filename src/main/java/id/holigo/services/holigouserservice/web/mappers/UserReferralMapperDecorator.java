package id.holigo.services.holigouserservice.web.mappers;

import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.web.model.UserReferralDto;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserReferralMapperDecorator implements UserReferralMapper {

    private UserReferralMapper userReferralMapper;

    @Autowired
    public void setUserReferralMapper(UserReferralMapper userReferralMapper) {
        this.userReferralMapper = userReferralMapper;
    }

    public UserReferralDto userReferralToUserReferralDto(UserReferral userReferral) {
        UserReferralDto userReferralDto = userReferralMapper.userReferralToUserReferralDto(userReferral);
        if (userReferral.getUser().getUserPersonal().getPhotoProfile() != null) {
            userReferralDto.setPhotoProfileUrl(userReferral.getUser().getUserPersonal().getPhotoProfile().getFileDownloadUri());
        }
        if (userReferral.getUser().getIsOfficialAccount()) {
            userReferralDto.setNote("128K followers");
        }
        return userReferralDto;
    }
}
