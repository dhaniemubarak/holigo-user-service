package id.holigo.services.holigouserservice.web.model;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import id.holigo.services.holigouserservice.web.validators.UniqueEmail;
import id.holigo.services.holigouserservice.web.validators.UniquePhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {
    
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
