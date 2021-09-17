package id.holigo.services.common.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import id.holigo.services.holigouserservice.validators.UniqueEmail;
import id.holigo.services.holigouserservice.validators.UniquePhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// import id.holigo.services.holigouserservice.validators.UniqueEmail;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

    @Null
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @UniquePhoneNumber
    private String phoneNumber;

    @Email
    @UniqueEmail
    private String email;

    @NotBlank
    private String type;



    
}
