package com.mipt.sharipovrazil.validator;

import com.mipt.sharipovrazil.dto.validation.DueDateNotBeforeCreation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, LocalDate> {

    private LocalDate creationDate;

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean isValid(LocalDate dueDate, ConstraintValidatorContext context) {
        if (dueDate == null || creationDate == null) {
            return true;
        }
        return !dueDate.isBefore(creationDate);
    }
}