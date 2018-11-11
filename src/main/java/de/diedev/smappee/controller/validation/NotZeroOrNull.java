package de.diedev.smappee.controller.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotZeroOrNullValidator.class)
public @interface NotZeroOrNull {
	Class<?>[] groups() default {};

	String message() default "amount is either zero or null";

	Class<? extends Payload>[] payload() default {};
}