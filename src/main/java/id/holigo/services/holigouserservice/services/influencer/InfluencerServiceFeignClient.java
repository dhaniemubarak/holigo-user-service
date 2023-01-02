package id.holigo.services.holigouserservice.services.influencer;

import id.holigo.services.holigouserservice.web.model.InfluencerFollowerDto;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "holigo-backoffice-service", url = "10.1.1.15:8127")
public interface InfluencerServiceFeignClient {

    String UPDATE_INFLUENCER_FOLLOWER_PATH = "/api/v1/management/influencer/users/followers";

    String UPDATE_INFLUENCER_PROFILE_PICTURE = "/api/v1/management/influencer/users/{userId}/userProfilePicture";

    @RequestMapping(method = RequestMethod.PUT, value = UPDATE_INFLUENCER_FOLLOWER_PATH)
    ResponseEntity<HttpStatus> updateFollower(@RequestParam("phoneNumber") String phoneNumber, @RequestBody InfluencerFollowerDto influencerFollowerDto);

    @RequestMapping(method = RequestMethod.PUT, value = UPDATE_INFLUENCER_PROFILE_PICTURE)
    ResponseEntity<HttpStatus> updateProfilePicture(@PathVariable("userId") Long userId, @RequestBody UserPersonalPhotoProfileDto userPersonalPhotoProfileDto);


}
