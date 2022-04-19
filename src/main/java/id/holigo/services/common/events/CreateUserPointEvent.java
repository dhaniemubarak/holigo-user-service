package id.holigo.services.common.events;

import id.holigo.services.common.model.UserPointDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreateUserPointEvent implements Serializable {

    static final long serialVersionUID = 36L;

    private UserPointDto userPointDto;

    public CreateUserPointEvent(UserPointDto userPointDto) {
        this.userPointDto = userPointDto;
    }
}
