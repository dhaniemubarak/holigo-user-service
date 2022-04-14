package id.holigo.services.common.events;

import java.io.Serializable;

import id.holigo.services.common.model.UpdateUserGroupDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserGroupEvent implements Serializable {

    static final long serialVersionUID = 4567356856L;

    private UpdateUserGroupDto updateUserGroupDto;

    public UserGroupEvent(UpdateUserGroupDto updateUserGroupDto) {
        this.updateUserGroupDto = updateUserGroupDto;
    }

}
