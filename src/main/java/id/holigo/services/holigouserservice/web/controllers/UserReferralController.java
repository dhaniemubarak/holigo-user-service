package id.holigo.services.holigouserservice.web.controllers;

import java.util.Objects;
import java.util.Optional;

import id.holigo.services.holigouserservice.domain.QueueUserReferralPoint;
import id.holigo.services.holigouserservice.repositories.QueueUserReferralPointRepository;
import id.holigo.services.holigouserservice.web.exceptions.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import id.holigo.services.holigouserservice.domain.UserReferral;
import id.holigo.services.holigouserservice.repositories.UserReferralRepository;
import id.holigo.services.holigouserservice.services.UserReferralService;
import id.holigo.services.holigouserservice.web.mappers.UserReferralMapper;
import id.holigo.services.holigouserservice.web.model.UserReferralDto;

@Slf4j
@RestController
public class UserReferralController {

    private UserReferralRepository userReferralRepository;

    private UserReferralMapper userReferralMapper;

    private UserReferralService userReferralService;

    private QueueUserReferralPointRepository queueUserReferralPointRepository;

    @Autowired
    public void setQueueUserReferralPointRepository(QueueUserReferralPointRepository queueUserReferralPointRepository) {
        this.queueUserReferralPointRepository = queueUserReferralPointRepository;
    }

    @Autowired
    public void setUserReferralRepository(UserReferralRepository userReferralRepository) {
        this.userReferralRepository = userReferralRepository;
    }

    @Autowired
    public void setUserReferralMapper(UserReferralMapper userReferralMapper) {
        this.userReferralMapper = userReferralMapper;
    }

    @Autowired
    public void setUserReferralService(UserReferralService userReferralService) {
        this.userReferralService = userReferralService;
    }

    @GetMapping("/api/v1/userReferral")
    public ResponseEntity<UserReferralDto> findReferral(@RequestParam("referral") String referral) {
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByReferral(referral);
        return fetchUserReferral.map(userReferral -> new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(userReferral),
                HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/api/v1/userReferral")
    public ResponseEntity<UserReferralDto> createReferral(@RequestHeader("user-id") Long userId) {

        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByUserId(userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (fetchUserReferral.isPresent()) {
            UserReferral userReferral = fetchUserReferral.get();
            httpHeaders.setLocation(
                    UriComponentsBuilder.fromPath("/api/v1/userReferral/{id}").buildAndExpand(userReferral.getId())
                            .toUri());
        } else {
            UserReferral userReferral = userReferralService.createRandomReferral(userId);
            httpHeaders.setLocation(
                    UriComponentsBuilder.fromPath("/api/v1/userReferral/{id}").buildAndExpand(userReferral.getId())
                            .toUri());
        }
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/users/{userId}/userReferral")
    public ResponseEntity<UserReferralDto> getReferralFromUser(@PathVariable("userId") Long id,
                                                               @RequestHeader("user-id") Long userId) {
        UserReferral userReferral;
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByUserId(id);
        if (fetchUserReferral.isPresent()) {
            userReferral = fetchUserReferral.get();
            if (!userReferral.getUser().getId().equals(userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(userReferral),
                HttpStatus.OK);
    }

    @GetMapping({"/api/v1/userReferral/{userReferralId}", "/api/v1/users/{userId}/userReferral/{userReferralId}"})
    public ResponseEntity<UserReferralDto> getReferral(@PathVariable("userReferralId") Long id,
                                                       @RequestHeader("user-id") Long userId, @PathVariable("userId") Long userIdTwo) {
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findById(id);
        if (fetchUserReferral.isPresent()) {
            UserReferral userReferral = fetchUserReferral.get();
            if (userReferral.getUser().getId().equals(userId)) {
                return new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(userReferral),
                        HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @PutMapping({"/api/v1/userReferral/{userReferralId}", "/api/v1/users/{userId}/userReferral/{userReferralId}"})
    public ResponseEntity<UserReferralDto> updateReferral(@RequestBody UserReferralDto userReferralDto,
                                                          @PathVariable("userReferralId") Long id, @RequestHeader("user-id") Long userId, @PathVariable("userId") Long userIdTwo) {

        Optional<UserReferral> fetchUserReferralIsExists = userReferralRepository
                .findByReferral(userReferralDto.getReferral());
        if (fetchUserReferralIsExists.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findById(id);
        if (fetchUserReferral.isPresent()) {
            UserReferral userReferral = fetchUserReferral.get();
            if (userReferral.getChangeGranted() < 1) {
                throw new ForbiddenException();
            }
            if (userReferral.getUser().getId().equals(userId)) {
                userReferral.setReferral(userReferralDto.getReferral().toUpperCase());
                userReferral.setChangeGranted(userReferral.getChangeGranted() - 1);
                UserReferral updatedUserReferral = userReferralRepository.save(userReferral);
                return new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(updatedUserReferral),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/api/v1/users/{id}/userReferral")
    public ResponseEntity<HttpStatus> risePoint(@RequestBody UserReferralDto userReferralDto, @PathVariable("id") Long id,
                                                @RequestHeader("user-id") Long userId) {

        if (!Objects.equals(id, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findByUserId(userId);
        if (fetchUserReferral.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserReferral userReferral = fetchUserReferral.get();
        try {
            if (userReferral.getPoint() == null) {
                userReferral.setPoint(userReferralDto.getPoint());
            } else {
                userReferral.setPoint(userReferral.getPoint() + userReferralDto.getPoint());
            }
            userReferralRepository.save(userReferral);
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
            QueueUserReferralPoint queueUserReferralPoint = QueueUserReferralPoint.builder()
                    .userId(userId).point(userReferralDto.getPoint()).isRaised(false)
                    .build();
            queueUserReferralPointRepository.save(queueUserReferralPoint);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
