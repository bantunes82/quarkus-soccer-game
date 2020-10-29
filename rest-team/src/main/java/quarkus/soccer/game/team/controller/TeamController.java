package quarkus.soccer.game.team.controller;

import lombok.extern.jbosslog.JBossLog;
import quarkus.soccer.game.team.controller.mapper.TeamMapper;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;
import quarkus.soccer.game.team.domainobject.TeamDO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;
import quarkus.soccer.game.team.service.TeamService;
import quarkus.soccer.game.team.util.CountryCode;
import quarkus.soccer.game.team.util.Range;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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
    private TeamMapper teamMapper;

   @Inject
    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @GET
    @Path("/random")
    public Response findRandomTeam() throws EntityNotFoundException {
        TeamDO teamRandom = teamService.findRandom();

        return Response.ok(teamMapper.toTeamDTO(teamRandom)).build();
    }

    @GET
    @Path("/name/{name}")
    public Response findTeamByName(@PathParam("name") String name) {
        List<TeamDO> teams = teamService.findByName(name);

        return Response.ok(teamMapper.toTeamDTOList(teams)).build();
    }

    @GET
    @Path("/country/{countryCode}")
    public Response findTeamByCountryCode(@PathParam("countryCode") @CountryCode String countryCode, @QueryParam("pageIndex") int pageIndex, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<TeamDO> teams = teamService.findByCountryCode(countryCode, pageIndex, pageSize);

        return Response.ok(teamMapper.toTeamDTOList(teams)).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createTeam(@Valid TeamDTO teamDTO, @Context UriInfo uriInfo) {
        TeamDO teamDO = teamMapper.toTeamDO(teamDTO);
        TeamDO teamSaved = teamService.create(teamDO);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(teamSaved.getId()));

        log.debugf("New team created with URI %s", builder.build().toString());

        return Response.created(builder.build()).build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{id}")
    public Response updateTeam(@PathParam("id") Long teamId, @Valid TeamDTO teamDTO) throws EntityNotFoundException {
        TeamDO teamDO = teamMapper.toTeamDO(teamDTO);
        TeamDO teamUpdated = teamService.update(teamId, teamDO);

        return Response.ok(teamMapper.toTeamDTO(teamUpdated)).build();
    }

    @PATCH
    @Path("/{id}/level/{value}")
    public Response updateTeamLevel(@PathParam("id") Long teamId, @Range(min = 1.0, max = 10.0) @PathParam("value") Double level) throws EntityNotFoundException {
        TeamDO teamUpdated = teamService.updateLevel(teamId, level);

        return Response.ok(teamMapper.toTeamDTO(teamUpdated)).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteTeam(@PathParam("id") Long teamId) throws EntityNotFoundException {
        teamService.delete(teamId);

        return Response.noContent().build();
    }
}
