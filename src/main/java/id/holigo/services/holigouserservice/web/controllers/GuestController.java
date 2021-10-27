package id.holigo.services.holigouserservice.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import id.holigo.services.holigouserservice.services.guest.GuestService;
import id.holigo.services.holigouserservice.web.model.GuestRegisterDto;

@RestController
public class GuestController {

    @Autowired
    private GuestService guestService;

    @PostMapping("/api/v1/guests")
    public ResponseEntity<GuestRegisterDto> createGuest(@RequestBody GuestRegisterDto guestRegisterDto) {
        guestService.createGuest(guestRegisterDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
