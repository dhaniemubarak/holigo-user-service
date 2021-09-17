package id.holigo.services.holigouserservice.web.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.holigo.services.common.model.UserDto;
import id.holigo.services.holigouserservice.services.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping({ "/api/v1/users" })
@Validated
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping(produces = "application/json", path = { "/{id}" })
    public ResponseEntity<UserDto> show(@NotNull @PathVariable("id") Long id) {
        return new ResponseEntity<UserDto>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<UserDto> store(@NotNull @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<UserDto>(userService.save(userDto), HttpStatus.CREATED);
    }

    @PutMapping(path = { "/{id}" })
    public ResponseEntity<UserDto> update(@NotNull @PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<UserDto>(userService.update(id, userDto), HttpStatus.CREATED);
    }
}
