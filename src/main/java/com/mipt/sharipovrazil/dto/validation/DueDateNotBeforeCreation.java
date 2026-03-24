package com.mipt.sharipovrazil.dto.validation;

import com.mipt.sharipovrazil.validator.DueDateNotBeforeCreationValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Documented
public @interface DueDateNotBeforeCreation {
    String message() default "Due date cannot be before creation date";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}