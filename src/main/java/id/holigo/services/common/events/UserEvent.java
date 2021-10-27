package id.holigo.services.common.events;

import java.io.Serializable;

import id.holigo.services.common.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent implements Serializable {

    static final long serialVersionUID = -5815566940065181210L;

    private UserDto userDto;

}
