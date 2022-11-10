package id.holigo.services.holigouserservice.services.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "holigo-account-balance-service")
public interface PointServiceFeignClient {
    String INIT_POINT_PATH = "/api/v1/pointAccountBalance";

    @RequestMapping(method = RequestMethod.POST, value = INIT_POINT_PATH)
    ResponseEntity<HttpStatus> initPoint(@RequestHeader("user-id") Long userId);
}
