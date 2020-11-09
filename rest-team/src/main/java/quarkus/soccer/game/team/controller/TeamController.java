package quarkus.soccer.game.team.controller;

import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import quarkus.soccer.game.team.controller.mapper.TeamMapper;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
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
import java.net.URI;
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

    @Operation(summary = "Returns a random soccer team")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO.class, required = true)), description = "When there is at least one soccer team available")
    @APIResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorDTO.class, required = true, example = "{\"timestamp\": 1604906081.774793,\"errors\": {\"error; \": \"Could not find any team\"}}")), description = "When there is no soccer team available")
    @GET
    @Path("/random")
    public Response findRandomTeam() throws EntityNotFoundException {
        TeamDO teamRandom = teamService.findRandom();

        return Response.ok(teamMapper.toTeamDTO(teamRandom)).build();
    }

    @Operation(summary = "Returns a list of soccer teams that has the specified name")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO[].class, required = true)), description = "Returns a list of soccer teams for the specified name")
    @GET
    @Path("/name/{name}")
    public Response findTeamByName(@Parameter(description = "soccer team name", required = true) @PathParam("name") String name) {
        List<TeamDO> teams = teamService.findByName(name);

        return Response.ok(teamMapper.toTeamDTOList(teams)).build();
    }

    @Operation(summary = "Returns all the soccer teams for the specified Country Code")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO[].class, required = true)), description = "Returns a list the soccer teams for the specified Country Code")
    @GET
    @Path("/country/{countryCode}")
    public Response findTeamByCountryCode(@Parameter(description = "country code", required = true) @PathParam("countryCode") @CountryCode String countryCode,
                                          @Parameter(description = "page index") @QueryParam("pageIndex") int pageIndex,
                                          @Parameter(description = "page size") @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<TeamDO> teams = teamService.findByCountryCode(countryCode, pageIndex, pageSize);

        return Response.ok(teamMapper.toTeamDTOList(teams)).build();
    }

    @Operation(summary = "Create a soccer team")
    @APIResponse(responseCode = "201", description = "The URI of the created soccer team", headers = {@Header(description = "URI location of the created soccer team", schema = @Schema(implementation = URI.class))})
    @POST
    @Consumes(APPLICATION_JSON)
    public Response createTeam(@RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO.class)), description = "soccer team to be created") @Valid TeamDTO teamDTO, @Context UriInfo uriInfo) {
        TeamDO teamDO = teamMapper.toTeamDO(teamDTO);
        TeamDO teamSaved = teamService.create(teamDO);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(teamSaved.getId()));

        log.debugf("New team created with URI %s", builder.build().toString());

        return Response.created(builder.build()).build();
    }

    @Operation(summary = "Update a soccer team for the specified team id")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO.class, required = true)), description = "Returns the soccer team updated")
    @APIResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorDTO.class, required = true, example = "{\"timestamp\": 1604906081.774793,\"errors\": {\"error; \": \"Could not find team with id: {id}\"}}")), description = "When there is no soccer team available for the specified id")
    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{id}")
    public Response updateTeam(@Parameter(required = true, description = "soccer team id") @PathParam("id") Long teamId,
                               @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO.class)), description = "soccer team to be updated") @Valid TeamDTO teamDTO) throws EntityNotFoundException {
        TeamDO teamDO = teamMapper.toTeamDO(teamDTO);
        TeamDO teamUpdated = teamService.update(teamId, teamDO);

        return Response.ok(teamMapper.toTeamDTO(teamUpdated)).build();
    }

    @Operation(summary = "Update the soccer team level for the specified team id")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = TeamDTO.class, required = true)), description = "Returns the soccer team with the updated level")
    @APIResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorDTO.class, required = true, example = "{\"timestamp\": 1604906081.774793,\"errors\": {\"error; \": \"Could not find team with id: {id}\"}}")), description = "When there is no soccer team available for the specified id")
    @PATCH
    @Path("/{id}/level/{value}")
    public Response updateTeamLevel(@Parameter(required = true, description = "soccer team id") @PathParam("id") Long teamId,
                                    @Parameter(required = true, description = "soccer team level") @Range(min = 1.0, max = 10.0) @PathParam("value") Double level) throws EntityNotFoundException {
        TeamDO teamUpdated = teamService.updateLevel(teamId, level);

        return Response.ok(teamMapper.toTeamDTO(teamUpdated)).build();
    }

    @Operation(summary = "Delete the soccer team for the specified team id")
    @APIResponse(responseCode = "204")
    @APIResponse(responseCode = "404", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ErrorDTO.class, required = true, example = "{\"timestamp\": 1604906081.774793,\"errors\": {\"error; \": \"Could not find team with id: {id}\"}}")), description = "When there is no soccer team available for the specified id")
    @DELETE
    @Path("{id}")
    public Response deleteTeam(@Parameter(required = true, description = "soccer team id") @PathParam("id") Long teamId) throws EntityNotFoundException {
        teamService.delete(teamId);

        return Response.noContent().build();
    }
}
