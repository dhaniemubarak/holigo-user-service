package id.holigo.services.holigouserservice.web.controllers;

import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import id.holigo.services.common.model.*;
import id.holigo.services.holigouserservice.services.*;
import id.holigo.services.holigouserservice.services.holiclub.HoliclubService;
import id.holigo.services.holigouserservice.services.point.PointService;
import id.holigo.services.holigouserservice.web.exceptions.ForbiddenException;
import id.holigo.services.holigouserservice.web.model.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonalPhotoProfile;
import id.holigo.services.holigouserservice.repositories.UserPersonalPhotoProfileRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.oauth.OauthService;
import id.holigo.services.holigouserservice.services.otp.OtpService;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalPhotoProfileMapper;
import id.holigo.services.holigouserservice.web.requests.ChangePin;
import id.holigo.services.holigouserservice.web.requests.CreateNewPin;
import id.holigo.services.holigouserservice.web.requests.ResetPin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
public class UserController {

    private final UserService userService;

    private final OtpService otpService;

    private final UserPersonalService userPersonalService;

    private final UserRepository userRepository;

    private final UserPersonalPhotoProfileRepository userPersonalPhotoProfileRepository;

    private final UserMapper userMapper;


    private final UserPersonalPhotoProfileMapper userPersonalPhotoProfileMapper;

    private final UserDeviceService userDeviceService;


    private final OauthService oauthService;

    private final PointService pointService;

    private final HoliclubService holiclubService;

    private final UserReferralService userReferralService;

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    @PostMapping(produces = "application/json", path = {"/api/v1/users"})
    public ResponseEntity<OauthAccessTokenDto> saveUser(@NotNull @Valid @RequestBody UserDto userDto,
                                                        @RequestHeader(value = "user-id") Long userId) throws Exception {
        boolean isRegisterValid = otpService.isRegisterIdValid(userDto.getRegisterId(), userDto.getPhoneNumber());
        if (isRegisterValid) {
            OauthAccessTokenDto oauthAccessTokenDto;
            if (userId != null) {
                userDto.setId(userId);
            }
            userDto.setIsOfficialAccount(false);
            User savedUser = userService.save(userDto);
            userService.createOneTimePassword(savedUser, "0921");
            Collection<String> authorities = new ArrayList<>();
            savedUser.getAuthorities().forEach(authority -> authorities.add(authority.getRole()));
            UserAuthenticationDto userAuthenticationDto = UserAuthenticationDto.builder().id(savedUser.getId())
                    .phoneNumber(savedUser.getPhoneNumber()).accountStatus(savedUser.getAccountStatus())
                    .type(savedUser.getType()).authorities(authorities).oneTimePassword("0921")
                    .accountNonExpired(savedUser.getAccountNonExpired())
                    .accountNonLocked(savedUser.getAccountNonLocked())
                    .userGroup(savedUser.getUserGroup())
                    .credentialsNonExpired(savedUser.getCredentialsNonExpired()).enabled(savedUser.getEnabled())
                    .build();
            oauthAccessTokenDto = oauthService.createAccessToken(userAuthenticationDto);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(
                    UriComponentsBuilder.fromPath("/api/v1/users/{id}").buildAndExpand(savedUser.getId()).toUri());
            return new ResponseEntity<>(oauthAccessTokenDto, httpHeaders, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping(path = {"/api/v1/users/{id}"})
    public ResponseEntity<UserDto> updateUser(@NotNull @PathVariable("id") Long id,
                                              @Valid @RequestBody UserDto userDto,
                                              @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        return new ResponseEntity<>(userService.update(id, userDto), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = {"/api/v1/users/{id}"})
    public ResponseEntity<UserDtoForUser> getUser(@NotNull @PathVariable("id") Long id,
                                                  @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Optional<User> fetchUser = userRepository.findById(id);
        if (fetchUser.isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(userMapper.userToUserDtoForUser(fetchUser.get()), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = {"/api/v1/completeUsers/{id}"})
    public ResponseEntity<UserDto> getCompleteUser(@NotNull @PathVariable("id") Long id) {
        return new ResponseEntity<>(userMapper.userToUserDto(userRepository.getById(id)), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = {"/api/v1/users/{id}/userDevices"})
    public ResponseEntity<UserDevicePaginate> getUserDevices(@NotNull @PathVariable("id") Long id,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                             @RequestParam(value = "pageNumber", required = false) Integer pageNumber) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize < 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        UserDevicePaginate userDevicePaginate = userDeviceService.listDevice(id, PageRequest.of(pageNumber, pageSize));
        return new ResponseEntity<>(userDevicePaginate, HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = {"/api/v1/users/{id}/userPersonal"})
    public ResponseEntity<UserPersonalDto> getUserPersonal(@PathVariable("id") Long id) {
        UserPersonalDto result = userPersonalService.getUserPersonalByUserId(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", path = {"/api/v1/users/{id}/userPersonal"})
    public ResponseEntity<UserPersonalDto> createUserPersonal(@PathVariable("id") Long id,
                                                              @RequestBody UserPersonalDto userPersonalDto) throws Exception {
        return new ResponseEntity<>(userPersonalService.createUserPersonalByUserId(id, userPersonalDto),
                HttpStatus.CREATED);

    }

    @PutMapping(produces = "application/json", path = {"/api/v1/users/{id}/userPersonal/{personalId}"})
    public ResponseEntity<UserPersonalDto> updateUserPersonal(@PathVariable("id") Long id,
                                                              @PathVariable("personalId") Long personalId,
                                                              @RequestBody UserPersonalDto userPersonalDto,
                                                              @RequestHeader("user-id") Long userId) {
        if (id.equals(userId)) {
            throw new ForbiddenException();
        }
        return new ResponseEntity<>(userPersonalService.updateUserPersonal(personalId, userPersonalDto), HttpStatus.OK);

    }

    @GetMapping(path = {"/api/v1/users/{id}/pin"})
    public ResponseEntity<UserDto> pinCheckAvailability(@PathVariable("id") Long id,
                                                        @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User fetchUser = user.get();
        if (fetchUser.getPin() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = {"/api/v1/users/{id}/pin"})
    public ResponseEntity<UserDto> createNewPin(@PathVariable("id") Long id,
                                                @Valid @RequestBody CreateNewPin createNewPin,
                                                @RequestHeader("user-id") Long userId) throws Exception {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        userService.createNewPin(id, createNewPin);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = {"/api/v1/users/{id}/pin"})
    public ResponseEntity<UserDto> updatePin(@PathVariable("id") Long id,
                                             @Valid @RequestBody ChangePin changePin,
                                             @RequestHeader("user-id") Long userId)
            throws Exception {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        userService.updatePin(id, changePin);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = {"/api/v1/users/{id}/resetPin"})
    public ResponseEntity<OtpDto> resetPin(@PathVariable("id") Long id,
                                           @Valid @RequestBody ResetPin resetPin,
                                           @RequestHeader("user-id") Long userId)
            throws Exception {
        if (!id.equals(userId)) {
            throw new NotFoundException();
        }
        userService.resetPin(id, resetPin);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = {"/api/v1/users/{id}/userPersonal/{personalId}/photoProfile"})
    public ResponseEntity<?> uploadPhotoProfile(@PathVariable("id") Long id,
                                                @PathVariable("personalId") Long personalId,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestHeader("user-id") Long userId) throws Exception {

        if (!id.equals(userId)) {
            throw new NotFoundException();
        }
        // String fileName = fileStorageService.storeFile(file);
        UserPersonalPhotoProfileDto userPersonalPhotoProfileDto = userPersonalService.savePhotoProfile(personalId,
                file);

        String locationUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/" + id + "/userPersonal/" + personalId + "/photoProfile/"
                        + userPersonalPhotoProfileDto.getId())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", locationUri);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(path = {"/api/v1/users/{id}/userPersonal/{personalId}/photoProfile/{photoProfileId}"})
    public ResponseEntity<UserPersonalPhotoProfileDto> getPhotoProfileData(@PathVariable("id") Long id,
                                                                           @PathVariable("personalId") Long personalId,
                                                                           @PathVariable("photoProfileId") Long photoProfileId,
                                                                           @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Optional<UserPersonalPhotoProfile> fetchPhotoProfile = userPersonalPhotoProfileRepository
                .findById(photoProfileId);
        if (fetchPhotoProfile.isEmpty()) {
            throw new NotFoundException("Photo profile not found");
        }
        return new ResponseEntity<>(userPersonalPhotoProfileMapper
                .userPersonalPhotoProfileToUserPersonalPhotoProfileDto(fetchPhotoProfile.get()), HttpStatus.OK);
    }

    @DeleteMapping(path = {"/api/v1/users/{id}/userPersonal/{personalId}/photoProfile/{photoProfileId}"})
    public ResponseEntity<?> deletePhotoProfile(@PathVariable("id") Long id,
                                                @PathVariable("personalId") Long personalId,
                                                @PathVariable("photoProfileId") Long photoProfileId,
                                                @RequestHeader("user-id") Long userId) {
        if (!id.equals(userId)) {
            throw new ForbiddenException();
        }
        Optional<UserPersonalPhotoProfile> fetchPhotoProfile = userPersonalPhotoProfileRepository
                .findById(photoProfileId);
        if (fetchPhotoProfile.isEmpty()) {
            throw new NotFoundException("Photo profile not found");
        }
        if (userPersonalService.deletePhotoProfile(photoProfileId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    @PostMapping(path = {"/api/v1/users/{userId}/referral"})
    public ResponseEntity<HttpStatus> joinReferral(@RequestBody JoinReferralDto joinReferralDto,
                                                   @RequestHeader("user-id") Long userId,
                                                   @PathVariable("userId") Long id) {
        if (userId.longValue() != id.longValue()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = userRepository.getById(userId);
        UserDto userDto = userMapper.userToUserDto(user);
        userDto.setReferral(joinReferralDto.getReferral());
        userDto = userService.fetchReferral(userDto);
        User parent = userMapper.userDtoToUser(userDto);
        user.setParent(parent.getParent());
        user.setOfficialId(userDto.getOfficialId());
        user.setUserGroup(UserGroupEnum.NETIZEN);
        User updatedUser = userRepository.save(user);
        if (updatedUser.getParent() != null) {
            // setup point
            pointService.createPoint(userId);
            // setup holiclub
            holiclubService.createUserClub(UserClubDto.builder()
                    .userId(userId)
                    .userGroup(UserGroupEnum.NETIZEN).build());
            // create referral
            userReferralService.createRandomReferral(userId);

        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping({"/api/v1/users/{id}"})
    public ResponseEntity<HttpStatus> deleteUser(@RequestBody DeletedUserDto deletedUserDto,
                                                 @RequestHeader("user-id") Long userId,
                                                 @PathVariable("id") Long id) {
        if (!Objects.equals(userId, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> fetchUser = userRepository.findById(userId);
        if (fetchUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(fetchUser.get(), deletedUserDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}