package de.diedev.smappee.controller.validation;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotZeroOrNullValidator implements ConstraintValidator<NotZeroOrNull, BigDecimal> {

	@Override
	public void initialize(final NotZeroOrNull constraintAnnotation) {

	}

	@Override
	public boolean isValid(final BigDecimal value, final ConstraintValidatorContext context) {
		return (value != null) && (value.compareTo(BigDecimal.ZERO) != 0);
	}

}
