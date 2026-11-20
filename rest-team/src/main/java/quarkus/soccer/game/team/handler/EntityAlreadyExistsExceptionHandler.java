package quarkus.soccer.game.team.handler;

import lombok.extern.jbosslog.JBossLog;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.exception.EntityAlreadyExistsException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@JBossLog
@Provider
@ApplicationScoped
public class EntityAlreadyExistsExceptionHandler implements ExceptionMapper<EntityAlreadyExistsException> {

    public Response toResponse(EntityAlreadyExistsException exception) {
        log.debugf("Entity already exists: %s", exception.getMessage());

        ErrorDTO errorDTO = new ErrorDTO(Collections.singletonMap("error; ", exception.getMessage()));
        return Response.status(Response.Status.CONFLICT).entity(errorDTO).build();
    }
}
