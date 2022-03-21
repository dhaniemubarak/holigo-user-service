package id.holigo.services.holigouserservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import id.holigo.services.common.model.UserDeviceDto;
import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.DeviceTypeEnum;
import id.holigo.services.holigouserservice.domain.EmailStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserReferralRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPersonalRepository userPersonalRepository;

    @Autowired
    UserReferralRepository userReferralRepository;

    @Autowired
    UserService userService;

    UserDto userDto;

    UserDeviceDto userDeviceDto;

    User userReferral;

    UserPersonal userPersonalReferral;

    UserReferral referralUser;

    User createUser;

    User officialUser;

    List<UserDeviceDto> devices = new ArrayList<>();

    @BeforeEach
    void setUp() {

        userDeviceDto = UserDeviceDto.builder().deviceId("876asdtfuhsa").deviceType(DeviceTypeEnum.ANDROID)
                .manufacturer("Samsung").model("SM-9500").osVersion("9.0").build();
        devices.add(userDeviceDto);
        userDto = UserDto.builder().email("budi@gmail.com").id(10L).name("Budi").phoneNumber("62812345678")
                .mobileToken("srpigouvhenprmacuhmeipruhmcweogtmv9htpwevothieht3q493534tbn").referral("johndoe")
                .userDevices(devices).build();
        User officialUserData = User.builder().email("zam@gmail.com").id(3L).name("Zam").phoneNumber("629817726354")
                .mobileToken("9htpwevothieht3q493534tbn").build();
        officialUser = userRepository.save(officialUserData);

        userReferral = User.builder().id(1L).name("John Doe").email("johnDoe@gmail.com")
                .emailStatus(EmailStatusEnum.CONFIRMED)
                .type("USER").phoneNumber("085718187373").officialId(officialUser.getId()).build();
        userPersonalReferral = UserPersonal.builder().id(1L).name("John Doe").phoneNumber("085718187373")
                .email("johnDoe@gmail.com").build();
        referralUser = UserReferral.builder().id(10L).referral("johndoe").build();

    }

    @Transactional
    @Test
    void testSave() {
        UserPersonal savedUserPersonalReferral = userPersonalRepository.save(userPersonalReferral);
        userReferral.setUserPersonal(savedUserPersonalReferral);
        User savedUser = userRepository.save(userReferral);
        referralUser.setUser(savedUser);
        userReferralRepository.save(referralUser);
        try {
            createUser = userService.save(userDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(savedUser.getOfficialId(), officialUser.getId());
        assertEquals(savedUser.getId(), createUser.getParent().getId());
        assertEquals(savedUser.getOfficialId(), createUser.getOfficialId());
    }
}
