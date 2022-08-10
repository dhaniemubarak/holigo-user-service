package id.holigo.services.holigouserservice.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import id.holigo.services.common.model.*;
import id.holigo.services.holigouserservice.domain.*;
import id.holigo.services.holigouserservice.repositories.*;
import id.holigo.services.holigouserservice.services.guest.GuestServiceImpl;
import id.holigo.services.holigouserservice.services.holiclub.HoliclubService;
import id.holigo.services.holigouserservice.services.point.PointService;
import id.holigo.services.holigouserservice.web.model.DeletedUserDto;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import id.holigo.services.holigouserservice.services.otp.OtpService;
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
    @Value("${otp.provider.priority}")
    public String otpProviderPriority;
    @Value("${default.referral}")
    public String defaultReferral;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;
    private final UserDeviceMapper userDeviceMapper;
    private final UserDeviceRepository userDeviceRepository;
    private final UserReferralRepository userReferralRepository;
    private final UserPersonalService userPersonalService;
    private final OtpService otpService;
    private final UserReferralService userReferralService;
    private final HoliclubService holiclubService;
    private final PointService pointService;

    private final DeletedUserRepository deletedUserRepository;

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
        UserPersonal savedUserPersonal = userPersonalService.createUserPersona(userPersonal);
        if (savedUserPersonal.getId() == null) {
            throw new Exception("Failed save personal data");
        }

        if (userDto.getReferral() == null && !this.defaultReferral.isEmpty()) {
            userDto.setReferral(this.defaultReferral);
        }
        userDto = fetchReferral(userDto);
        User user = userMapper.userDtoToUser(userDto);
        user.setType("USER");
        user.setAccountStatus(AccountStatusEnum.ACTIVE);
        if (this.otpProviderPriority.equals("EMAIL")) {
            user.setEmailStatus(EmailStatusEnum.CONFIRMED);
            user.setEmailVerifiedAt(Timestamp.valueOf(LocalDateTime.now()));
            user.setEnabled(true);
        } else {
            user.setEmailStatus(INIT_EMAIL_STATUS);
            user.setVerificationCode(RandomStringUtils.randomAlphabetic(64));
        }

        user.setUserPersonal(savedUserPersonal);

        Optional<Authority> fetchAuth = authorityRepository.findById(2);
        GuestServiceImpl.signAuth(fetchAuth, user, userRepository);
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
        return userRepository.findAllByEmail(email).size() > 0;
    }

    @Override
    @Transactional
    public void deleteUser(User user, DeletedUserDto deletedUserDto) {
        boolean recursive = true;
        int phoneNumberCounter = 1;
        int emailCounter = 1;
        String deletedPhoneNumber = user.getPhoneNumber();
        String deletedEmail = user.getEmail();
        while (recursive) {
            deletedPhoneNumber = user.getPhoneNumber() + "-" + phoneNumberCounter;
            Optional<User> fetchPhoneNumber = userRepository.findByPhoneNumber(deletedPhoneNumber);

            if (fetchPhoneNumber.isEmpty()) {
                recursive = false;
            }
            phoneNumberCounter++;
        }
        recursive = true;
        while (recursive) {
            deletedEmail = user.getEmail() + "-" + emailCounter;
            Optional<User> fetchEmail = userRepository.findByEmail(deletedEmail);
            if (fetchEmail.isEmpty()) {
                recursive = false;
            }
            emailCounter++;
        }
        user.setPhoneNumber(deletedPhoneNumber);
        user.setEmail(deletedEmail);
        user.setAccountNonExpired(false);
        user.setAccountNonLocked(false);
        user.setCredentialsNonExpired(false);
        user.setEnabled(false);
        user.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setVerificationCode(null);
        user.setPin(null);
        user.setMobileToken(null);
        user.setOneTimePassword(null);
        user.setAccountStatus(AccountStatusEnum.DELETED);
        userRepository.save(user);
        deletedUserRepository.save(DeletedUser.builder()
                .userId(user.getId()).reason(deletedUserDto.getReason()).build());

    }


    @Override
    @Transactional
    public boolean isPhoneNumberAlreadyInUse(String phoneNumber) {
        return userRepository.findAllByPhoneNumber(phoneNumber).size() > 0;
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

        boolean isOtpValid = otpDto.getStatus() == OtpStatusEnum.CONFIRMED;
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
            UserReferral userReferral;
            UserParentDto parent;
            Long officialId = null;
            userReferral = userReferralRepository.findByReferral(userDto.getReferral())
                    .orElseThrow();

            userDto.setUserGroup(UserGroupEnum.NETIZEN);
            parent = userMapper.userToUserParentDto(userReferral.getUser());
            if (parent.getOfficialId() != null) {
                officialId = parent.getOfficialId();
            }
            userDto.setOfficialId(officialId);
            userDto.setParent(parent);
        }
        return userDto;
    }

}
