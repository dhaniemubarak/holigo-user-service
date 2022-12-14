package id.holigo.services.holigouserservice.repositories;

import id.holigo.services.holigouserservice.domain.QueueUserReferralFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueueUserReferralFollowerRepository extends JpaRepository<QueueUserReferralFollower, Long> {

    List<QueueUserReferralFollower> findAllByHasUpdate(Boolean updated);
}
