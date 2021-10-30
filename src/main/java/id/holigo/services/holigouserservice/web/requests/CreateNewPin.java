package id.holigo.services.holigouserservice.web.requests;

import org.springframework.format.annotation.NumberFormat;
import static org.springframework.format.annotation.NumberFormat.Style.NUMBER;

import id.holigo.services.holigouserservice.web.validators.EqualPin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualPin
public class CreateNewPin {

    @NumberFormat(style = NUMBER)
    private String pin;

    @NumberFormat(style = NUMBER)
    private String pinConfirmation;
}
