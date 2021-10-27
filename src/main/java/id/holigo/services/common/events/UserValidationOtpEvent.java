package id.holigo.services.common.events;

import id.holigo.services.common.model.UserDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserValidationOtpEvent extends UserEvent {

    public UserValidationOtpEvent(UserDto userDto) {
        super(userDto);
    }
}
