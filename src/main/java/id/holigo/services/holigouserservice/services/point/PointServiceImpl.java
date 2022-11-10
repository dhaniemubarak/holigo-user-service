package id.holigo.services.holigouserservice.services.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {

    private PointServiceFeignClient pointServiceFeignClient;

    @Autowired
    public void setPointServiceFeignClient(PointServiceFeignClient pointServiceFeignClient) {
        this.pointServiceFeignClient = pointServiceFeignClient;
    }

    @Override
    public void createPoint(Long userId) {
        pointServiceFeignClient.initPoint(userId);
    }
}
