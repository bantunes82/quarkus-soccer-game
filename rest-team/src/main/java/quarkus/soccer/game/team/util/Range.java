package quarkus.soccer.game.team.util;

import quarkus.soccer.game.team.validation.Validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = DoubleRangeValidator.class)
@Documented
public @interface Range
{
    String message() default Validation.TEAM_LEVEL_INVALID;

    double min() default Double.MIN_VALUE;

    double max() default Double.MAX_VALUE;

}
