package id.holigo.services.holigouserservice.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import id.holigo.services.common.model.*;
import id.holigo.services.holigouserservice.services.holiclub.HoliclubService;
import id.holigo.services.holigouserservice.services.point.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import id.holigo.services.holigouserservice.repositories.AuthorityRepository;
import id.holigo.services.holigouserservice.repositories.UserDeviceRepository;
import id.holigo.services.holigouserservice.repositories.UserReferralRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.otp.OtpService;
import id.holigo.services.holigouserservice.domain.Authority;
import id.holigo.services.holigouserservice.domain.EmailStatusEnum;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserDevice;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.web.exceptions.ForbiddenException;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserDeviceMapper;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;
import id.holigo.services.holigouserservice.web.requests.ResetPin;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    public static final EmailStatusEnum INIT_EMAIL_STATUS = EmailStatusEnum.WAITING_CONFIRMATION;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AuthorityRepository authorityRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final UserDeviceMapper userDeviceMapper;

    @Autowired
    private final UserDeviceRepository userDeviceRepository;

    @Autowired
    private final UserReferralRepository userReferralRepository;

    @Autowired
    final UserPersonalService userPersonalService;

    @Autowired
    private final OtpService otpService;

    @Autowired
    private final UserReferralService userReferralService;

    @Autowired
    private final HoliclubService holiclubService;

    @Autowired
    private final PointService pointService;

    @Override
    @Transactional
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Override
    @Transactional
    public User save(UserDto userDto) throws Exception {
        UserDevice userDevice = userDeviceMapper.userDeviceDtoToUserDevice(userDto.getUserDevices().get(0));
        UserPersonal userPersonal = new UserPersonal();
        userPersonal.setName(userDto.getName());
        userPersonal.setEmail(userDto.getEmail());
        userPersonal.setPhoneNumber(userDto.getPhoneNumber());
        userPersonal.setEmailStatus(INIT_EMAIL_STATUS);
        UserPersonal savedUserPersonal = userPersonalService.createUserPersona(userPersonal);
        if (savedUserPersonal.getId() == null) {
            throw new Exception("Failed save personal data");
        }
        userDto = fetchReferral(userDto);
        User user = userMapper.userDtoToUser(userDto);
        user.setType("USER");
        user.setAccountStatus(AccountStatusEnum.ACTIVE);
        user.setEmailStatus(INIT_EMAIL_STATUS);
        user.setUserPersonal(savedUserPersonal);

        Optional<Authority> fetchAuth = authorityRepository.findById(2);
        if (fetchAuth.isPresent()) {
            Authority auth = fetchAuth.get();
            Set<Authority> roles = new HashSet<>();
            roles.add(auth);
            user.setAuthorities(roles);
        }
        User userSaved = userRepository.save(user);

        if (userSaved.getId() != null) {
            if (userSaved.getParent() != null) {
                pointService.createPoint(userSaved.getId());
                holiclubService.createUserClub(UserClubDto.builder()
                        .userId(userSaved.getId())
                        .userGroup(UserGroupEnum.NETIZEN).build());
                userReferralService.createRandomReferral(userSaved.getId());
            }
            userDevice.setUser(userSaved);
            userDeviceRepository.save(userDevice);
            return userSaved;
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
        // resultUser.setType(userDto.getType());
        if (resultUser.getParent() != null) {
            fetchReferral(userDto);
        }
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
        return userMapper
                .userToUserDto(userRepository.findByPhoneNumber(phoneNumber).orElseThrow(NotFoundException::new));
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
        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        if (user.getPin() != null) {
            throw new ForbiddenException("PIN has been set!");
        }
        user.setPin(createNewPin.getPin());
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("Failed set PIN");
        }
        return userMapper.userToUserDto(user);
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

    @Override
    public void addAuthorityToUser(String phoneNumber, String role) {
        Optional<User> fetchUser = userRepository.findByPhoneNumber(phoneNumber);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        Authority authority = authorityRepository.findByRole(role);
        user.getAuthorities().add(authority);
    }

    @Override
    public void createOneTimePassword(User user, String oneTimePassword) {
        user.setOneTimePassword(new BCryptPasswordEncoder().encode(oneTimePassword));
        userRepository.save(user);

    }

    @Override
    public void resetOneTimePassword(User user) {
        user.setOneTimePassword(null);
        userRepository.save(user);

    }

    @Override
    public UserDto resetPin(Long userId, ResetPin resetPin) throws Exception {
        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = fetchUser.get();
        OtpDto otpDto = otpService.getOtpForResetPin(OtpDto.builder().phoneNumber(user.getPhoneNumber())
                .oneTimePassword(resetPin.getOneTimePassword()).build());

        boolean isOtpValid = false;
        if (otpDto.getStatus() == OtpStatusEnum.CONFIRMED) {
            isOtpValid = true;
        }
        if (isOtpValid) {
            user.setPin(resetPin.getPin());
            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new Exception("Failed set PIN");
            }
        } else {
            throw new ForbiddenException("OTP is wrong!");
        }
        return userMapper.userToUserDto(user);
    }

    public UserDto fetchReferral(UserDto userDto) {
        userDto.setUserGroup(UserGroupEnum.MEMBER);
        if (userDto.getReferral() != null) {
            UserReferral userReferral = null;
            UserDto parent = null;
            Long officialId = null;
            userReferral = userReferralRepository.findByReferral(userDto.getReferral())
                    .orElseThrow();

            userDto.setUserGroup(UserGroupEnum.NETIZEN);
            parent = userMapper.userToUserDto(userReferral.getUser());
            if (parent.getOfficialId() != null) {
                officialId = parent.getOfficialId();
            }
            userDto.setOfficialId(officialId);
            userDto.setParent(parent);
        }
        return userDto;
    }

}
