package id.holigo.services.common.events;

import java.io.Serializable;

import id.holigo.services.common.model.UserAuthenticationDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class UserAuthenticationEvent implements Serializable {

    static final long serialVersionUID = -1556665181210L;

    private UserAuthenticationDto userAuthenticationDto;

    public UserAuthenticationEvent(UserAuthenticationDto userAuthenticationDto) {
        this.userAuthenticationDto = userAuthenticationDto;
    }

}
