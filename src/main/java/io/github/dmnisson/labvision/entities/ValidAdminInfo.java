package io.github.dmnisson.labvision.entities;

import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint( validatedBy = ValidAdminInfoValidator.class )
@Documented
public @interface ValidAdminInfo {
	String message() default "Admin must have a valid email address or phone number set.";
	
	Class<?>[] groups() default { };
	
	Class<? extends Payload>[] payload() default { };
}
