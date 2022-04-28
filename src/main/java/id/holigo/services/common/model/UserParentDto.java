package id.holigo.services.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserParentDto implements Serializable {
    static final long serialVersionUID = -65181210L;

    private Long id;

    private Long officialId;

    private String name;

    private String phoneNumber;

    private String email;

    private EmailStatusEnum emailStatus;

    private AccountStatusEnum accountStatus;

    private String mobileToken;
}
