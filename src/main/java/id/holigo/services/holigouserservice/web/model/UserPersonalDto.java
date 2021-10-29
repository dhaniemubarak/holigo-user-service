package id.holigo.services.holigouserservice.web.model;

import java.sql.Date;

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
    
    private GenderEnum gender;

    private Date birthDate;

    private String city;
}
