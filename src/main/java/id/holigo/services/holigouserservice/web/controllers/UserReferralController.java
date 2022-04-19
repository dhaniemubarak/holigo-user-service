package id.holigo.services.holigouserservice.web.controllers;

import java.util.Optional;
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

@RestController
public class UserReferralController {

    @Autowired
    private UserReferralRepository userReferralRepository;

    @Autowired
    private UserReferralMapper userReferralMapper;

    @Autowired
    private UserReferralService userReferralService;

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
            userReferral = userReferralService.createRandomReferral(userId);
        }
        return new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(userReferral),
                HttpStatus.OK);
    }

    @GetMapping({"/api/v1/userReferral/{userReferralId}", "/api/v1/users/{userId}/userReferral/{userReferralId}"})
    public ResponseEntity<UserReferralDto> getReferral(@PathVariable("userReferralId") Long id,
                                                       @RequestHeader("user-id") Long userId) {
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
                                                          @PathVariable("userReferralId") Long id, @RequestHeader("user-id") Long userId) {

        Optional<UserReferral> fetchUserReferralIsExists = userReferralRepository
                .findByReferral(userReferralDto.getReferral());
        if (fetchUserReferralIsExists.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<UserReferral> fetchUserReferral = userReferralRepository.findById(id);
        if (fetchUserReferral.isPresent()) {
            UserReferral userReferral = fetchUserReferral.get();
            if (userReferral.getUser().getId().equals(userId)) {
                userReferral.setReferral(userReferralDto.getReferral().toUpperCase());
                UserReferral updatedUserReferral = userReferralRepository.save(userReferral);
                return new ResponseEntity<>(userReferralMapper.userReferralToUserReferralDto(updatedUserReferral),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
