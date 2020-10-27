package quarkus.soccer.game.team.handler;

import lombok.extern.jbosslog.JBossLog;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@JBossLog
@Provider
@ApplicationScoped
public class EntityNotFoundExceptionHandler implements ExceptionMapper<EntityNotFoundException> {

    public Response toResponse(EntityNotFoundException exception) {
        log.debugf("Errors while finding entity: %s", exception.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(Collections.singletonMap("error; ", exception.getMessage()));
        return Response.status(Response.Status.NOT_FOUND).entity(errorDTO).build();
    }
}
