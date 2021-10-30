package id.holigo.services.holigouserservice.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import id.holigo.services.common.model.AccountStatusEnum;
import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.repositories.UserDeviceRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserDevice;
import id.holigo.services.holigouserservice.web.exceptions.ForbiddenException;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserDeviceMapper;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.model.UserRegisterDto;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final UserDeviceMapper userDeviceMapper;

    @Autowired
    private final UserDeviceRepository userDeviceRepository;

    @Override
    @Transactional
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        UserDevice userDevice = userDeviceMapper.userDeviceDtoToUserDevice(userDto.getUserDevices().get(0));

        userDto.setType("USER");
        userDto.setAccountStatus(AccountStatusEnum.ACTIVE);
        userDto.setEmailStatus(EmailStatusEnum.WAITING_CONFIRMATION);
        User userSaved = userRepository.save(userMapper.userDtoToUser(userDto));
        if (userSaved.getId() != null) {
            userDevice.setUser(userSaved);
            userDeviceRepository.save(userDevice);
            return userMapper.userToUserDto(userSaved);
        }
        return null;

    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User resultUser = userRepository.findById(id).orElseThrow(NotFoundException::new);
        resultUser.setName(userDto.getName());
        resultUser.setEmail(userDto.getEmail());
        resultUser.setPhoneNumber(userDto.getPhoneNumber());
        resultUser.setType(userDto.getType());
        return userMapper.userToUserDto(userRepository.save(resultUser));
    }

    @Override
    @Transactional
    public boolean isEmailAlreadyInUse(String email) {
        if (userRepository.findAllByEmail(email).size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isPhoneNumberAlreadyInUse(String phoneNumber) {
        if (userRepository.findAllByPhoneNumber(phoneNumber).size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public UserDto getByPhoneNumber(String phoneNumber) {
        log.info("findByPhoneNumber called.......");
        return userMapper
                .userToUserDto(userRepository.findByPhoneNumber(phoneNumber).orElseThrow(NotFoundException::new));
    }

    @Override
    public UserRegisterDto createUserViaOtp(UserRegisterDto userRegisterDto) {
        //
        return null;
    }

    @Override
    public UserPaginate getAllUser() {
        UserPaginate userPaginate;
        List<UserDto> users = userRepository.findAll().stream().map(userMapper::userToUserDto)
                .collect(Collectors.toList());

        userPaginate = new UserPaginate(users);

        return userPaginate;
    }

    @Override
    public UserDto createNewPin(Long userId, CreateNewPin createNewPin) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        if (fetchUser.getPin() != null) {
            throw new ForbiddenException("PIN has been set!");
        }

        ;
        fetchUser.setPin(createNewPin.getPin());
        try {
            userRepository.save(fetchUser);
        } catch (Exception e) {
            throw new Exception("Failed set PIN");
        }
        return userMapper.userToUserDto(fetchUser);
    }

    @Override
    public UserDto updatePin(Long userId, ChangePin changePin) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        boolean isValid = new BCryptPasswordEncoder().matches(changePin.getCurrentPin(), fetchUser.getPin());
        if (!isValid) {
            throw new ForbiddenException("Current PIN not valid");
        }
        fetchUser.setPin(changePin.getPin());
        try {
            userRepository.save(fetchUser);
        } catch (Exception e) {
            throw new Exception("Failed change PIN");
        }
        return userMapper.userToUserDto(fetchUser);
    }

}
