package id.holigo.services.holigouserservice.web.model;

import id.holigo.services.common.model.AccountStatusEnum;
import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.common.model.UserDeviceDto;
import id.holigo.services.common.model.UserGroupEnum;
import id.holigo.services.holigouserservice.web.validators.UniqueEmail;
import id.holigo.services.holigouserservice.web.validators.UniquePhoneNumber;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDtoForUser implements Serializable {

    static final long serialVersionUID = -5815566940065181210L;
    @Null
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @UniquePhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email
    @UniqueEmail
    private String email;

    private EmailStatusEnum emailStatus;

    private AccountStatusEnum accountStatus;

    private String mobileToken;

    private String type;

    private Long registerId;

    private String referral;

    private UserGroupEnum userGroup;

    List<UserDeviceDto> userDevices;

}
