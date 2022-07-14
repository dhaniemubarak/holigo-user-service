package id.holigo.services.holigouserservice.services;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import id.holigo.services.common.model.UserGroupEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserReferralRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class UserReferralServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPersonalRepository userPersonalRepository;

    @Autowired
    UserReferralService userReferralService;

    @Autowired
    UserReferralRepository userReferralRepository;

    User user;
    UserPersonal userPersonal;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("John Doe").email("johnDoe@gmail.com").emailStatus(EmailStatusEnum.CONFIRMED)
                .type("USER").phoneNumber("085718187373").userGroup(UserGroupEnum.MEMBER).build();
        userPersonal = UserPersonal.builder().id(1L).name("John Doe").phoneNumber("085718187373")
                .email("johnDoe@gmail.com").build();
    }

    @Transactional
    @Test
    void testCreateRandomReferral() {
        UserPersonal savedUserPersonal = userPersonalRepository.save(userPersonal);
        user.setUserPersonal(savedUserPersonal);
        User savedUser = userRepository.save(user);

        userReferralService.createRandomReferral(savedUser.getId());

        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByUserId(savedUser.getId());

        assertTrue(fetchUserReferral.isPresent());
        if (fetchUserReferral.isPresent()) {
            log.info("userReferral -> {}", fetchUserReferral.get());
        }

    }
}
