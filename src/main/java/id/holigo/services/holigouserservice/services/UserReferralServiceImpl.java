package id.holigo.services.holigouserservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.holigo.services.holigouserservice.domain.ReferralStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.repositories.UserReferralRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import io.netty.util.internal.ThreadLocalRandom;

@Service
public class UserReferralServiceImpl implements UserReferralService {

    private UserReferralRepository userReferralRepository;

    private UserRepository userRepository;
    @Autowired
    public void setUserReferralRepository(UserReferralRepository userReferralRepository) {
        this.userReferralRepository = userReferralRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserReferral createRandomReferral(Long userId) {

        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByUserId(userId);
        if (fetchUserReferral.isEmpty()) {
            Optional<User> fetchUser = userRepository.findById(userId);
            if (fetchUser.isPresent()) {
                String referral = generateRandomReferral(fetchUser.get());
                UserReferral userReferral = new UserReferral();
                userReferral.setUser(fetchUser.get());
                userReferral.setReferral(referral);
                userReferral.setStatus(ReferralStatusEnum.ACTIVE);
                userReferral.setChangeGranted(0);
                userReferral.setFollowers(0);
                return userReferralRepository.save(userReferral);
            } else {
                throw new NotFoundException("User not found");
            }
        }
        return fetchUserReferral.get();
    }

    private String generateRandomReferral(User user) {
        String name = user.getName();
        name = name.replaceAll("\\s+", "");
        if (name.length() > 4) {
            name = name.substring(0, 5);
        }
        int number = randomNumber();
        boolean isUnavailable = true;
        String referral;
        do {
            referral = name.toUpperCase() + number;
            number++;
            Optional<UserReferral> fetchUserReferral = userReferralRepository.findByReferral(referral);
            if (fetchUserReferral.isEmpty()) {
                isUnavailable = false;
            }
        } while (isUnavailable);

        return referral;
    }

    private int randomNumber() {
        return ThreadLocalRandom.current().nextInt(0, 9998 + 1);
    }

}
