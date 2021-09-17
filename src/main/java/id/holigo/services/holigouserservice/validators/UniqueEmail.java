package id.holigo.services.holigouserservice.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UniqueEmailValidator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface UniqueEmail {
    public String message() default "Email already been taken";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
