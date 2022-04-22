package id.holigo.services.holigouserservice.web.model;

import lombok.*;

import javax.validation.constraints.Null;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserParentForUser implements Serializable {

    static final long serialVersionUID = -65181210L;
    @Null
    private Long id;

    @Null
    private Long officialId;

    private String name;

    private String email;
}
