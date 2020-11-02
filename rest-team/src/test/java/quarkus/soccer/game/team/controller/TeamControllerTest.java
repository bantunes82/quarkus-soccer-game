package quarkus.soccer.game.team.controller;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quarkus.soccer.game.team.controller.mapper.TeamMapper;
import quarkus.soccer.game.team.datatransferobject.CountryDTO;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;
import quarkus.soccer.game.team.domainobject.CountryDO;
import quarkus.soccer.game.team.domainobject.TeamDO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;
import quarkus.soccer.game.team.service.TeamService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;
    @Mock
    private TeamMapper teamMapper;
    @InjectMocks
    private TeamController teamController;

    private CountryDO countryDO;
    private CountryDTO countryDTO;
    private TeamDO teamDO;
    private TeamDTO teamDTO;

    @BeforeEach
    void setUp() {
        countryDO = new CountryDO(1l, "Brasil", "BR");
        countryDTO = new CountryDTO("Brasil", "BR");

        teamDO = TeamDO.builder()
                .id(1L)
                .name("Sport Club Corinthians Paulista")
                .nickName("Timao")
                .founded(LocalDate.of(1910, 9, 1))
                .level(9.8)
                .picture("https://conteudo.imguol.com.br//c/esporte/futebol/times/desktop/corinthians.jpg")
                .countryDO(countryDO)
                .deleted(false)
                .build();

        teamDTO = TeamDTO.builder()
                .name("Sport Club Corinthians Paulista")
                .nickName("Timao")
                .founded(LocalDate.of(1910, 9, 1))
                .level(9.8)
                .picture("https://conteudo.imguol.com.br//c/esporte/futebol/times/desktop/corinthians.jpg")
                .countryDTO(countryDTO)
                .build();
    }

    @Test
    void findRandomTeam_ReturnsTeam() throws EntityNotFoundException {
        when(teamService.findRandom()).thenReturn(teamDO);
        when(teamMapper.toTeamDTO(teamDO)).thenReturn(teamDTO);

        Response response = teamController.findRandomTeam();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Sport Club Corinthians Paulista", ((TeamDTO) response.getEntity()).getName());
    }

    @Test
    void findRandomTeam_GivenThereIsNoTeam_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(teamService.findRandom()).thenThrow(new EntityNotFoundException("Could not find any team"));

        Assertions.assertThrows(EntityNotFoundException.class, () -> teamController.findRandomTeam(), "Could not find any team");
    }

    @Test
    void findTeamByName_GivenValidTeamName_ReturnsTeamList() {
        when(teamService.findByName("Sport Club Corinthians Paulista")).thenReturn(Arrays.asList(teamDO));
        when(teamMapper.toTeamDTOList(Arrays.asList(teamDO))).thenReturn(Arrays.asList(teamDTO));

        Response response = teamController.findTeamByName("Sport Club Corinthians Paulista");

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, ((List<TeamDTO>) response.getEntity()).size());
        Assertions.assertEquals("Sport Club Corinthians Paulista", ((List<TeamDTO>) response.getEntity()).get(0).getName());
    }

    @Test
    void findTeamByName_GivenInvalidTeamName_ReturnsEmptyTeamList() {
        when(teamService.findByName("XXX")).thenReturn(Collections.emptyList());
        when(teamMapper.toTeamDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        Response response = teamController.findTeamByName("XXX");

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(0, ((List<TeamDTO>) response.getEntity()).size());
    }

    @Test
    void findTeamByCountryCode_GivenExistentCountryCode_ReturnsTeamList() {
        when(teamService.findByCountryCode("BR", 0, 10)).thenReturn(Arrays.asList(teamDO));
        when(teamMapper.toTeamDTOList(Arrays.asList(teamDO))).thenReturn(Arrays.asList(teamDTO));

        Response response = teamController.findTeamByCountryCode("BR", 0, 10);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, ((List<TeamDTO>) response.getEntity()).size());
        Assertions.assertEquals("Sport Club Corinthians Paulista", ((List<TeamDTO>) response.getEntity()).get(0).getName());
    }

    @Test
    void findTeamByCountryCode_GivenNoExistentCountryCode_ReturnsEmptyTeamList() {
        when(teamService.findByCountryCode("BR", 0, 10)).thenReturn(Collections.emptyList());
        when(teamMapper.toTeamDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        Response response = teamController.findTeamByCountryCode("BR", 0, 10);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(0, ((List<TeamDTO>) response.getEntity()).size());
    }

    @Test
    void createTeam_ReturnsTeam() {
        when(teamMapper.toTeamDO(teamDTO)).thenReturn(teamDO);
        when(teamService.create(teamDO)).thenReturn(teamDO);

        UriInfo uriInfo = new ResteasyUriInfo("/rest-team/v1/teams", "");

        Response response = teamController.createTeam(teamDTO, uriInfo);

        Assertions.assertEquals(201, response.getStatus());
        Assertions.assertEquals("/rest-team/v1/teams/1", response.getLocation().toString());
    }

    @Test
    void updateTeam_GivenValidTeamId_ReturnsTeam() throws EntityNotFoundException {
        when(teamMapper.toTeamDO(teamDTO)).thenReturn(teamDO);
        when(teamService.update(1L, teamDO)).thenReturn(teamDO);
        when(teamMapper.toTeamDTO(teamDO)).thenReturn(teamDTO);

        Response response = teamController.updateTeam(1L, teamDTO);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Sport Club Corinthians Paulista", ((TeamDTO) response.getEntity()).getName());
    }

    @Test
    void updateTeam_GivenInvalidTeamId_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(teamMapper.toTeamDO(teamDTO)).thenReturn(teamDO);
        when(teamService.update(1L, teamDO)).thenThrow(new EntityNotFoundException("Could not find team with id: 1"));

        Assertions.assertThrows(EntityNotFoundException.class, () -> teamController.updateTeam(1L, teamDTO), "Could not find team with id: 1");
    }

    @Test
    void updateTeamLevel_GivenValidTeamId_ReturnsTeam() throws EntityNotFoundException {
        when(teamService.updateLevel(1L, 8d)).thenReturn(teamDO);
        when(teamMapper.toTeamDTO(teamDO)).thenReturn(teamDTO);

        Response response = teamController.updateTeamLevel(1L, 8d);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Sport Club Corinthians Paulista", ((TeamDTO) response.getEntity()).getName());
    }

    @Test
    void updateTeamLevel_GivenInvalidTeamId_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        when(teamService.updateLevel(1L, 8d)).thenThrow(new EntityNotFoundException("Could not find team with id: 1"));

        Assertions.assertThrows(EntityNotFoundException.class, () -> teamController.updateTeamLevel(1L, 8d), "Could not find team with id: 1");
    }


    @Test
    void deleteTeam_GivenValidTeamId_DeleteTeam() throws EntityNotFoundException {
        doNothing().when(teamService).delete(1L);

        Response response = teamController.deleteTeam(1L);

        Assertions.assertEquals(204, response.getStatus());
    }

    @Test
    void deleteTeam_GivenInvalidTeamId_ThrowsEntityNotFoundException() throws EntityNotFoundException {
        doThrow(new EntityNotFoundException("Could not find team with id: 1")).when(teamService).delete(1L);

        Assertions.assertThrows(EntityNotFoundException.class, () -> teamController.deleteTeam(1L), "Could not find team with id: 1");
    }
}