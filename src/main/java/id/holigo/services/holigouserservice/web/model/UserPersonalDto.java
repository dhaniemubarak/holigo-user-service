package id.holigo.services.holigouserservice.web.model;

import java.sql.Date;

import id.holigo.services.holigouserservice.domain.EmailStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalDto {
    private Long id;

    private String name;

    private String phoneNumber;

    private String email;

    private EmailStatusEnum emailStatus;

    private GenderEnum gender;

    private Date birthDate;

    private String city;

    private UserPersonalPhotoProfilDto photoProfil;
}
