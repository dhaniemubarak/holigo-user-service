package id.holigo.services.holigouserservice.web.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import id.holigo.services.holigouserservice.web.validators.EqualPin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualPin
public class CreateNewPin extends Pin {

    @Size(min = 6, max = 6)
    @NotBlank
    @Pattern(regexp = "^([+-]?(\\d+)([,.]\\d+)?)?$" )
    private String pinConfirmation;
}
