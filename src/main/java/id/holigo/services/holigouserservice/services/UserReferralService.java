package id.holigo.services.holigouserservice.services;

import id.holigo.services.holigouserservice.domain.UserReferral;

public interface UserReferralService {
    UserReferral createRandomReferral(Long userId);
}
