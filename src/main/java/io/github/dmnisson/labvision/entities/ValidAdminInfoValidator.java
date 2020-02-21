package io.github.dmnisson.labvision.entities;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidAdminInfoValidator implements ConstraintValidator<ValidAdminInfo, AdminInfo> {

	@Override
	public boolean isValid(AdminInfo adminInfo, ConstraintValidatorContext context) {
		if (Objects.isNull(adminInfo)) {
			return true;
		}
		
		return Objects.nonNull(adminInfo.getEmail()) || Objects.nonNull(adminInfo.getPhone());
	}

}
