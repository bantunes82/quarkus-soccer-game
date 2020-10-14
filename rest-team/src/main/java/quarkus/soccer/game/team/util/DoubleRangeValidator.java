package quarkus.soccer.game.team.util;

import org.jboss.logging.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DoubleRangeValidator implements ConstraintValidator<Range, Object> {

    private static final Logger LOGGER = Logger.getLogger(DoubleRangeValidator.class);

    private Double maxPrecision;
    private Double minPrecision;
    private String message;

    @Override
    public void initialize(Range doubleRange) {
        maxPrecision = doubleRange.max();
        minPrecision = doubleRange.min();
        message = doubleRange.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean isValid = false;

        if (object instanceof Double) {
            Double value = Double.valueOf(object.toString());
            isValid = value.compareTo(maxPrecision) <= 0 && value.compareTo(minPrecision) >= 0;

            if (!isValid) {
                LOGGER.warnf("Range is invalid: %s", value);
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
            }
        }

        return isValid;
    }
}
