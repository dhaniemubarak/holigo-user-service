package id.holigo.services.holigouserservice.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import id.holigo.services.common.model.*;
import id.holigo.services.holigouserservice.services.*;
import id.holigo.services.holigouserservice.services.holiclub.HoliclubService;
import id.holigo.services.holigouserservice.services.point.PointService;
import id.holigo.services.holigouserservice.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private final UserService userService;

    @Autowired
    private final OtpService otpService;

    @Autowired
    private final UserPersonalService userPersonalService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPersonalPhotoProfileRepository userPersonalPhotoProfileRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final UserPersonalPhotoProfileMapper userPersonalPhotoProfileMapper;

    @Autowired
    private final UserDeviceService userDeviceService;

    @Autowired
    private final OauthService oauthService;

    @Autowired
    private final PointService pointService;

    @Autowired
    private final HoliclubService holiclubService;

    @Autowired
    private final UserReferralService userReferralService;

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    public List<UserDto> index(@RequestParam(value = "phoneNumber", required = true) String phoneNumber) {
        log.info("Calling index ....");
        List<UserDto> users = new ArrayList<>();
        if (!phoneNumber.isEmpty()) {
            log.info("Not empty");
            System.out.println("Not Empty");
            return userRepository.findAllByPhoneNumber(phoneNumber).stream().map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        }
        return users;
    }

    @GetMapping(path = {"/api/v1/users"})
    public ResponseEntity<UserPaginate> getAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", path = {"/api/v1/users"})
    public ResponseEntity<OauthAccessTokenDto> saveUser(@NotNull @Valid @RequestBody UserDto userDto,
                                                        @RequestHeader(value = "user-id") Long userId) throws Exception {
        boolean isRegisterValid = otpService.isRegisterIdValid(userDto.getRegisterId(), userDto.getPhoneNumber());
        if (isRegisterValid) {
            OauthAccessTokenDto oauthAccessTokenDto = null;
            if (userId != null) {
                userDto.setId(userId);
            }
            User savedUser = userService.save(userDto);
            userService.createOneTimePassword(savedUser, "0921");
            Collection<String> authorities = new ArrayList<>();
            savedUser.getAuthorities().forEach(authority -> {
                authorities.add(authority.getRole());
            });
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
                                              @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<UserDto>(userService.update(id, userDto), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = {"/api/v1/users/{id}"})
    public ResponseEntity<UserDtoForUser> getUser(@NotNull @PathVariable("id") Long id) {
        return new ResponseEntity<>(userMapper.userToUserDtoForUser(userRepository.getById(id)), HttpStatus.OK);
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
                                                              @PathVariable("personalId") Long personalId, @RequestBody UserPersonalDto userPersonalDto) {
        return new ResponseEntity<>(userPersonalService.updateUserPersonal(personalId, userPersonalDto), HttpStatus.OK);

    }

    @GetMapping(path = {"/api/v1/users/{id}/pin"})
    public ResponseEntity<UserDto> pinCheckAvailability(@PathVariable("id") Long id) {
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
                                                @Valid @RequestBody CreateNewPin createNewPin) throws Exception {
        userService.createNewPin(id, createNewPin);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(path = {"/api/v1/users/{id}/pin"})
    public ResponseEntity<UserDto> updatePin(@PathVariable("id") Long id, @Valid @RequestBody ChangePin changePin)
            throws Exception {
        userService.updatePin(id, changePin);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = {"/api/v1/users/{id}/resetPin"})
    public ResponseEntity<OtpDto> resetPin(@PathVariable("id") Long id, @Valid @RequestBody ResetPin resetPin)
            throws Exception {
        userService.resetPin(id, resetPin);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(path = {"/api/v1/users/{id}/userPersonal/{personalId}/photoProfile"})
    public ResponseEntity<?> uploadPhotoProfile(@PathVariable("id") Long id,
                                                @PathVariable("personalId") Long personalId, @RequestParam("file") MultipartFile file) throws Exception {
        // String fileName = fileStorageService.storeFile(file);
        UserPersonalPhotoProfileDto userPersonalPhotoProfileDto = userPersonalService.savePhotoProfile(personalId,
                file);

        String locationUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/" + Long.toString(id) + "/userPersonal/" + personalId + "/photoProfile/"
                        + userPersonalPhotoProfileDto.getId())
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", locationUri);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(path = {"/api/v1/users/{id}/userPersonal/{personalId}/photoProfile/{photoProfileId}"})
    public ResponseEntity<UserPersonalPhotoProfileDto> getPhotoProfileData(@PathVariable("id") Long id,
                                                                           @PathVariable("personalId") Long personalId, @PathVariable("photoProfileId") Long photoProfileId) {
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
                                                @PathVariable("personalId") Long personalId, @PathVariable("photoProfileId") Long photoProfileId) {
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

    @GetMapping(path = {"/api/v1/users/{id}/photoProfile/{fileName:.+}"})
    public ResponseEntity<Resource> getPhotoProfile(@PathVariable("id") Long id,
                                                    @PathVariable("fileName") String fileName, HttpServletRequest request) {
        Resource resource = userPersonalService.getPhotoProfile(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            //
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                // resource.getFilename() + "\"")
                .body(resource);
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
        log.info("Parent -> {}", updatedUser.getParent());
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


}