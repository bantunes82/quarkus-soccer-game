package quarkus.soccer.game.team.handler;

import lombok.extern.jbosslog.JBossLog;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@JBossLog
@Provider
@ApplicationScoped
public class ConstraintViolationExceptionHandler
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.debugf("Errors while beans validation: %s", exception.getMessage());

        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(constraintViolation -> constraintViolation.getPropertyPath().toString(), ConstraintViolation::getMessage, (existing, replacement) -> existing));

        ErrorDTO errorDTO = new ErrorDTO(errors);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
    }

}