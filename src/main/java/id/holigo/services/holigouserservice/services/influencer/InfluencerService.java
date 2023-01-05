package id.holigo.services.holigouserservice.services.influencer;

import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfileDto;

public interface InfluencerService {

    void updateInfluencerFollower(String phoneNumber, int follower);

    void updateProfilePicture(Long userId, UserPersonalPhotoProfileDto userPersonalPhotoProfileDto);

    void updateReferralCode(String phoneNumber, String referralCode);
}
