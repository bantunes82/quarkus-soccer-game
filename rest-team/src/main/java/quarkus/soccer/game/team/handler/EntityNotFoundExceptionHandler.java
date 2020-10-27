package quarkus.soccer.game.team.handler;

import org.jboss.logging.Logger;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
@ApplicationScoped
public class EntityNotFoundExceptionHandler implements ExceptionMapper<EntityNotFoundException> {

    private static final Logger LOGGER = Logger.getLogger(EntityNotFoundExceptionHandler.class);

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        LOGGER.warnf("Errors while finding entity: %s", exception.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(Collections.singletonMap("error; ", exception.getMessage()));
        return Response.status(Response.Status.NOT_FOUND).entity(errorDTO).build();
    }
}
