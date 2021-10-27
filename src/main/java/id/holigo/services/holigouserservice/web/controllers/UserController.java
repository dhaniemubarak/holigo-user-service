package id.holigo.services.holigouserservice.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.UserDeviceService;
import id.holigo.services.holigouserservice.services.UserPersonalService;
import id.holigo.services.holigouserservice.services.UserService;
import id.holigo.services.holigouserservice.web.mappers.UserMapper;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalMapper;
import id.holigo.services.holigouserservice.web.model.UserDevicePaginate;
import id.holigo.services.holigouserservice.web.model.UserPaginate;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
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
    private final UserPersonalService userPersonalService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPersonalRepository userPersonalRepository;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final UserPersonalMapper userPersonalMapper;

    @Autowired
    private final UserDeviceService userDeviceService;

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

    @GetMapping(path = { "/api/v1/users" })
    public ResponseEntity<UserPaginate> getAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", path = { "/api/v1/users" })
    public ResponseEntity<UserDto> saveUser(@NotNull @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<UserDto>(userService.save(userDto), HttpStatus.CREATED);
    }

    @PutMapping(path = { "/api/v1/users/{id}" })
    public ResponseEntity<UserDto> updateUser(@NotNull @PathVariable("id") Long id,
            @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<UserDto>(userService.update(id, userDto), HttpStatus.OK);
    }

    // @PostMapping(path = "/api/v2/users")
    // public String register(@RequestBody UserDto userDto) {
    // userOtpService.checkForOtpIsValid(userDto);
    // return "Ok";
    // }

    @GetMapping(produces = "application/json", path = { "/api/v1/users/{id}" })
    public ResponseEntity<UserDto> getUser(@NotNull @PathVariable("id") Long id) {
        return new ResponseEntity<UserDto>(userService.findById(id), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json", path = { "/api/v1/users/{id}/userDevices" })
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

    @GetMapping(produces = "application/json", path = { "/api/v1/users/{id}/userPersonal" })
    public ResponseEntity<UserPersonalDto> getUserPersonal(@PathVariable("id") Long id) {
        UserPersonalDto result = userPersonalService.getUserPersonalByUserId(id);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
        // Optional<User> user = userRepository.findById(id);
        // if (user.isPresent()) {
            
        //     User fetchUser = user.get();
        //     if (result == null) {
        //         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        //     }
        //     UserPersonal userPersonal = fetchUser.getUserPersonal();
        //     return new ResponseEntity<>(userPersonalMapper.userPersonalToUserPersonalDto(userPersonal), HttpStatus.OK);
        // }
        // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(produces = "application/json", path = { "/api/v1/users/{id}/userPersonal" })
    public ResponseEntity<UserPersonalDto> createUserPersonal(@PathVariable("id") Long id,
            @RequestBody UserPersonalDto userPersonalDto) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            UserPersonal userPersonal = userPersonalRepository
                    .save(userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto));
            if (userPersonal.getId() != null) {
                User fetchUser = user.get();
                fetchUser.setUserPersonal(userPersonal);
                userRepository.save(fetchUser);
                return new ResponseEntity<>(userPersonalMapper.userPersonalToUserPersonalDto(userPersonal),
                        HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(produces = "application/json", path = { "/api/v1/users/{id}/userPersonal/{personalId}" })
    public ResponseEntity<UserPersonalDto> updateUserPersonal(@PathVariable("id") Long id,
            @PathVariable("personalId") Long personalId, @RequestBody UserPersonalDto userPersonalDto) {
        Optional<UserPersonal> userPersonal = userPersonalRepository.findById(personalId);
        if (userPersonal.isPresent()) {
            UserPersonal fetchUserPersonal = userPersonal.get();
            UserPersonal updateUserPersonal = userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto);
            updateUserPersonal.setId(fetchUserPersonal.getId());
            log.info("user personal dto -> {}", updateUserPersonal);
            UserPersonal userPersonalUpdated = userPersonalRepository.save(updateUserPersonal);
            return new ResponseEntity<>(userPersonalMapper.userPersonalToUserPersonalDto(userPersonalUpdated),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
}
