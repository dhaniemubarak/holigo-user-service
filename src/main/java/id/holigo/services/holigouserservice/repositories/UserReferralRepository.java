package id.holigo.services.holigouserservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import id.holigo.services.holigouserservice.domain.UserReferral;

public interface UserReferralRepository extends JpaRepository<UserReferral, Long> {
    Optional<UserReferral> findByUserId(Long userId);

    Optional<UserReferral> findByReferral(String referral);
}
