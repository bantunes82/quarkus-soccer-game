package quarkus.soccer.game.team.util;

import lombok.extern.jbosslog.JBossLog;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Locale;

@JBossLog
public class CountryCodeValidator implements ConstraintValidator<CountryCode, Object> {

    private String message;

    @Override
    public void initialize(CountryCode doubleRange) {
        message = doubleRange.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean isValid = false;

        if (object instanceof String) {
            String value = object.toString();
            isValid = Arrays.stream(Locale.getISOCountries())
                    .anyMatch(code -> code.equals(value));

            if (!isValid) {
                log.debugf("Country Code is invalid: %s", value);
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
            }
        }

        return isValid;
    }
}
