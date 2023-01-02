package id.holigo.services.holigouserservice.services.influencer;

import id.holigo.services.holigouserservice.web.model.InfluencerFollowerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "holigo-backoffice-service", url = "10.1.1.15:8127")
public interface InfluencerServiceFeignClient {

    String UPDATE_INFLUENCER_FOLLOWER_PATH = "/api/v1/management/influencer/users/followers";

    @RequestMapping(method = RequestMethod.PUT, value = UPDATE_INFLUENCER_FOLLOWER_PATH)
    ResponseEntity<HttpStatus> updateFollower(@RequestParam("phoneNumber") String phoneNumber, @RequestBody InfluencerFollowerDto influencerFollowerDto);
}
