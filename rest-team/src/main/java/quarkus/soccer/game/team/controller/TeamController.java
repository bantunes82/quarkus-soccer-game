package quarkus.soccer.game.team.controller;

import lombok.extern.jbosslog.JBossLog;
import quarkus.soccer.game.team.domainobject.TeamDO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;
import quarkus.soccer.game.team.service.TeamService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@JBossLog
@Path("/v1/teams")
@Produces(APPLICATION_JSON)
public class TeamController {

    private TeamService teamService;

    @Inject
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GET
    @Path("/random")
    public Response findRandomTeam() throws EntityNotFoundException {
        TeamDO random = teamService.findRandom();

        return Response.ok(random).build();
    }

    @GET
    @Path("/name/{name}")
    public Response findTeamByName(@PathParam("name") String name) {
        List<TeamDO> teams = teamService.findByName(name);

        return Response.ok(teams).build();
    }

    @GET
    @Path("/country/{countryCode}")
    public Response findTeamByCountryCode(@PathParam("countryCode") String countryCode, @QueryParam("pageIndex") int pageIndex, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<TeamDO> teams = teamService.findByCountryCode(countryCode, pageIndex, pageSize);

        return Response.ok(teams).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createTeam(@Valid TeamDO teamDO, @Context UriInfo uriInfo) {
        TeamDO teamSaved = teamService.create(teamDO);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(teamSaved.getId()));

        log.debugf("New team created with URI %s", builder.build().toString());

        return Response.created(builder.build()).build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{id}")
    public Response updateTeam(@PathParam("id") Long teamId, @Valid TeamDO team) throws EntityNotFoundException {
        TeamDO teamUpdated = teamService.update(teamId, team);

        return Response.ok(teamUpdated).build();
    }
}
