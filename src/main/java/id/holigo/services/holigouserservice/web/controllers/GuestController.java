package id.holigo.services.holigouserservice.web.controllers;

import java.util.ArrayList;
import java.util.Collection;

import javax.jms.JMSException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import id.holigo.services.common.model.OauthAccessTokenDto;
import id.holigo.services.common.model.UserAuthenticationDto;
import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.services.UserService;
import id.holigo.services.holigouserservice.services.guest.GuestService;
import id.holigo.services.holigouserservice.services.oauth.OauthService;
import id.holigo.services.holigouserservice.web.model.GuestRegisterDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GuestController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final OauthService oauthService;

    @PostMapping("/api/v1/guests")
    public ResponseEntity<OauthAccessTokenDto> createGuest(@RequestBody GuestRegisterDto guestRegisterDto)
            throws JsonProcessingException, JMSException {
        User guest = guestService.createGuest(guestRegisterDto);

        if (guest.getId() != null) {
            OauthAccessTokenDto oauthAccessTokenDto;
            userService.createOneTimePassword(guest, "0921");
            Collection<String> authorities = new ArrayList<>();
            guest.getAuthorities().forEach(authority -> authorities.add(guest.getType()));
            UserAuthenticationDto userAuthenticationDto = UserAuthenticationDto.builder().id(guest.getId())
                    .phoneNumber(guest.getPhoneNumber()).accountStatus(guest.getAccountStatus()).type(guest.getType())
                    .authorities(authorities).oneTimePassword("0921")
                    .accountNonExpired(guest.getAccountNonExpired())
                    .accountNonLocked(guest.getAccountNonLocked())
                    .credentialsNonExpired(guest.getCredentialsNonExpired()).enabled(guest.getEnabled())
                    .userGroup(guest.getUserGroup()).build();

            oauthAccessTokenDto = oauthService.createAccessToken(userAuthenticationDto);
            guest.setPhoneNumber(null);
            guest.setOneTimePassword(null);
            guestService.updateGuest(guest);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(
                    UriComponentsBuilder.fromPath("/api/v1/guests/{id}").buildAndExpand(guest.getId()).toUri());
            return new ResponseEntity<>(oauthAccessTokenDto, httpHeaders, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api/v1/guests/{id}")
    public ResponseEntity<UserDto> getGuest(@PathVariable("id") Long id) {
        UserDto user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
