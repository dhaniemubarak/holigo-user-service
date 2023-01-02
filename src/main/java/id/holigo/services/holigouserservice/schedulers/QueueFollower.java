package id.holigo.services.holigouserservice.schedulers;

import id.holigo.services.holigouserservice.domain.QueueUserReferralFollower;
import id.holigo.services.holigouserservice.repositories.QueueUserReferralFollowerRepository;
import id.holigo.services.holigouserservice.services.UserReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueueFollower {

    private QueueUserReferralFollowerRepository queueUserReferralFollowerRepository;

    private UserReferralService userReferralService;

    @Autowired
    public void setUserReferralService(UserReferralService userReferralService) {
        this.userReferralService = userReferralService;
    }

    @Autowired
    public void setUserReferralFollowerRepository(QueueUserReferralFollowerRepository queueUserReferralFollowerRepository) {
        this.queueUserReferralFollowerRepository = queueUserReferralFollowerRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void updateFollower() {
        List<QueueUserReferralFollower> userReferralFollowers = queueUserReferralFollowerRepository.findAllByHasUpdate(false);
        userReferralFollowers.forEach(queueUserReferralFollower -> {
            boolean updated = userReferralService.updateUserFollower(queueUserReferralFollower.getUserId());
            if (updated) {
                queueUserReferralFollower.setHasUpdate(true);
                queueUserReferralFollowerRepository.save(queueUserReferralFollower);
            }
        });
    }
}
