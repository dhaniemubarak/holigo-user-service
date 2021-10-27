package id.holigo.services.common.events;

import id.holigo.services.common.model.UserDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OtpValidatedEvent extends UserEvent {
    OtpValidatedEvent(UserDto userDto) {
        super(userDto);
    }
}
