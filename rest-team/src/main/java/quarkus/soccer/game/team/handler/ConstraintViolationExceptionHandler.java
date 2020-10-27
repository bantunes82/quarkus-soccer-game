package quarkus.soccer.game.team.handler;

import org.jboss.logging.Logger;
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

@Provider
@ApplicationScoped
public class ConstraintViolationExceptionHandler
        implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = Logger.getLogger(ConstraintViolationExceptionHandler.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LOGGER.warnf("Errors while beans validation: %s", exception.getMessage());

        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(constraintViolation -> lastFieldName(constraintViolation.getPropertyPath().iterator()), ConstraintViolation::getMessage));

        ErrorDTO errorDTO = new ErrorDTO(errors);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorDTO).build();
    }

    private String lastFieldName(Iterator<Path.Node> nodes) {
        Path.Node last = null;
        while (nodes.hasNext()) {
            last = nodes.next();
        }
        return last.getName();
    }
}