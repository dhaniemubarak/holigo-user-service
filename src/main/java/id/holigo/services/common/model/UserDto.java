package id.holigo.services.common.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

    @Null
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    private String email;

    @NotBlank
    private String type;



    
}
