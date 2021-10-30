package id.holigo.services.holigouserservice.web.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import id.holigo.services.holigouserservice.web.requests.CreateNewPin;

public class EqualPinValidator implements ConstraintValidator<EqualPin, CreateNewPin> {

    @Override
    public boolean isValid(CreateNewPin value, ConstraintValidatorContext context) {
        return value.getPin().equals(value.getPinConfirmation());
    }

}
