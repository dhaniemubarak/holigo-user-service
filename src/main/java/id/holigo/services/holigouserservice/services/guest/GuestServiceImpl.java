package id.holigo.services.holigouserservice.services.guest;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.holigo.services.common.model.AccountStatusEnum;
import id.holigo.services.common.model.UserGroupEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserDevice;
import id.holigo.services.holigouserservice.repositories.UserDeviceRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.web.mappers.UserDeviceMapper;
import id.holigo.services.holigouserservice.web.model.GuestRegisterDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestServiceImpl implements GuestService {

    private UserRepository userRepository;

    private UserDeviceMapper userDeviceMapper;

    private UserDeviceRepository userDeviceRepository;

    @Autowired
    public void setUserDeviceMapper(UserDeviceMapper userDeviceMapper) {
        this.userDeviceMapper = userDeviceMapper;
    }

    @Autowired
    public void setUserDeviceRepository(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User createGuest(GuestRegisterDto guestRegisterDto) {
        String name;

        UserDevice userDevice = userDeviceMapper.userDeviceDtoToUserDevice(guestRegisterDto.getUserDevices().get(0));
        name = "Guest " + userDevice.getManufacturer() + " " + userDevice.getModel();

//        Optional<Authority> fetchAuth = authorityRepository.findById(1);

        User user = new User();
        user.setMobileToken(guestRegisterDto.getMobileToken());
        user.setPhoneNumber(RandomStringUtils.random(15, true, true));
        user.setName(name);
        user.setType("GUEST");
        user.setAccountStatus(AccountStatusEnum.ACTIVE);
        user.setUserGroup(UserGroupEnum.MEMBER);
        user.addUserDevice(userDevice);
        user.setIsOfficialAccount(false);

//        signAuth(fetchAuth, user, userRepository);
        User userSaved = userRepository.save(user);
        if (userSaved.getId() != null) {
            userDevice.setUser(userSaved);
            userDeviceRepository.save(userDevice);
            return userSaved;
        }
        return null;
    }

//    public static void signAuth(Optional<Authority> fetchAuth, User user, UserRepository userRepository) {
//        if (fetchAuth.isPresent()) {
//            Authority auth = fetchAuth.get();
//            Set<Authority> roles = new HashSet<>();
//            roles.add(auth);
//            user.setAuthorities(roles);
//        }
//
//    }

    public void updateGuest(User user) {
        userRepository.save(user);
    }
}
