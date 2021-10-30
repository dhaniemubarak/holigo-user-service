package id.holigo.services.holigouserservice.web.validators;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static java.lang.annotation.ElementType.TYPE;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = EqualPinValidator.class)
@Retention(RUNTIME)
@Target({ TYPE })
public @interface EqualPin {
    public String message() default "Pin mismatch !";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
