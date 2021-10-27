package id.holigo.services.holigouserservice.services.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDeviceMapper userDeviceMapper;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Transactional
    @Override
    public User createGuest(GuestRegisterDto guestRegisterDto) {
        String name;
        User user = new User();
        user.setMobileToken(guestRegisterDto.getMobileToken());
        UserDevice userDevice = userDeviceMapper.userDeviceDtoToUserDevice(guestRegisterDto.getUserDevices().get(0));
        name = "Guest " + userDevice.getManufacturer() + userDevice.getModel();
        user.setName(name);
        user.setType("GUEST");
        user.addUserDevice(userDevice);
        User userSaved = userRepository.save(user);
        userDevice.setUser(userSaved);
        userDeviceRepository.save(userDevice);
        return userSaved;
    }

}
