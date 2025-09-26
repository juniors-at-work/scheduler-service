package com.example.scheduler.adapters.validator;

import com.example.scheduler.adapters.annotation.TimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalTime;

public class TimeRangeValidator implements ConstraintValidator<TimeRange, Object> {
    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(TimeRange constraintAnnotation) {
        startFieldName = constraintAnnotation.startField();
        endFieldName = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field startTimeField = value.getClass().getDeclaredField(startFieldName);
            startTimeField.setAccessible(true);

            Field endTimeField = value.getClass().getDeclaredField(endFieldName);
            endTimeField.setAccessible(true);

            LocalTime startTime = (LocalTime) startTimeField.get(value);
            LocalTime endTime = (LocalTime) endTimeField.get(value);
            return startTime.isBefore(endTime);

        } catch (Exception ignored) {
            return false;
        }
    }
}
