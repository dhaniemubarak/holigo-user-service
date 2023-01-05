package id.holigo.services.holigouserservice.services.influencer;

import id.holigo.services.holigouserservice.web.model.InfluencerDto;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfluencerServiceImpl implements InfluencerService {

    private InfluencerServiceFeignClient influencerServiceFeignClient;

    @Autowired
    public void setInfluencerServiceFeignClient(InfluencerServiceFeignClient influencerServiceFeignClient) {
        this.influencerServiceFeignClient = influencerServiceFeignClient;
    }

    @Override
    public void updateInfluencerFollower(String phoneNumber, int follower) {
        InfluencerDto influencerDto = InfluencerDto.builder().holigoFollower(follower).build();
        influencerServiceFeignClient.updateFollower(phoneNumber, influencerDto);
    }

    @Override
    public void updateProfilePicture(Long userId, UserPersonalPhotoProfileDto userPersonalPhotoProfileDto) {
        influencerServiceFeignClient.updateProfilePicture(userId, userPersonalPhotoProfileDto);
    }

    @Override
    public void updateReferralCode(String phoneNumber, String referralCode) {
        InfluencerDto influencerDto = InfluencerDto.builder().referralCode(referralCode).build();
    }
}
