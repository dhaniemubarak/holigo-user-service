package id.holigo.services.holigouserservice.web.validators;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UniquePhoneNumberValidator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface UniquePhoneNumber {
    public String message() default "Phone number already been taken";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
