package quarkus.soccer.game.team.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import quarkus.soccer.game.team.container.DatabaseResource;
import quarkus.soccer.game.team.datatransferobject.CountryDTO;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeamControllerIntegrationTest {

    private String TEAM_PATH = "/v1/teams";
    private String TEAM_NAME = "Sport Club Corinthians Paulista";

    private CountryDTO germany;
    private CountryDTO argentina;
    private CountryDTO invalidCountryDTO;
    private CountryDTO invalidCountryDTONullValues;

    private TeamDTO bayernMunchen;
    private TeamDTO bocaJuniors;
    private TeamDTO invalidTeamDTO;
    private TeamDTO invalidTeamDTONullValues;
    private TeamDTO invalidTeamDTONullCountryDTO;

    @BeforeEach
    void setUp() {
        createBocaJuniorsTeam();
        createBayerMunchenTeam();
        createInvalidTeamDTO();
        createInvalidTeamDTONullValues();
        createInvalidTeamDTONullCountryDTO();

    }

    @Test
    @Order(1)
    void findRandomTeam_GivenThereIsTeam_ReturnsOK() {
        TeamDTO response = given()
                .when()
                .get(TEAM_PATH + "/random")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(TeamDTO.class);

        Assertions.assertEquals(TEAM_NAME, response.getName());

    }

    @Test
    @Order(2)
    void findTeamByName_GivenNoExistentTeamName_ReturnsEmptyTeamList() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/name/XXXXX")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(0, response.size());
    }

    @Test
    @Order(3)
    void findTeamByName_GivenExistentTeamName_ReturnsTeamListWithOneElement() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/name/Sport Club Corinthians Paulista")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(TEAM_NAME, response.get(0).getName());
    }

    @Test
    @Order(4)
    void findTeamByCountryCode_GivenNoExistentTeamWithTheCountryCode_ReturnsEmptyTeamList() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/country/AR")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(0, response.size());
    }

    @Test
    @Order(5)
    void findTeamByCountryCode_GivenInvalidCountryCode_ReturnsBadRequest() {
        ErrorDTO response = given()
                .when()
                .get(TEAM_PATH + "/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("findTeamByCountryCode.arg0"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(6)
    void findTeamByCountryCode_GivenInvalidCountryCode_ReturnsBadRequestSpanish() {
        ErrorDTO response = given()
                .when()
                .header(ACCEPT_LANGUAGE, "es")
                .get(TEAM_PATH + "/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Codigo del país debe tener 2 caracteres, ser un código existente y en Mayúsculo", response.getErrors().get("findTeamByCountryCode.arg0"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(7)
    void findTeamByCountryCode_GivenExistentTeamWithTheCountryCode_ReturnsTeamListWithOneElement() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/country/BR")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(TEAM_NAME, response.get(0).getName());
    }

    @Test
    @Order(10)
    void createTeam_GivenInvalidDTOValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTO)
                .post(TEAM_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country name must be between 3 and 20 chars", response.getErrors().get("createTeam.arg0.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("createTeam.arg0.countryDTO.code"));
        Assertions.assertEquals("Team founded date must be older than today", response.getErrors().get("createTeam.arg0.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.arg0.level"));
        Assertions.assertEquals("Team name must be between 3 and 30 chars", response.getErrors().get("createTeam.arg0.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(11)
    void createTeam_GivenInvalidDTONullValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTONullValues)
                .post(TEAM_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(6, response.getErrors().size());
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("createTeam.arg0.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("createTeam.arg0.countryDTO.code"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("createTeam.arg0.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.arg0.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("createTeam.arg0.name"));
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("createTeam.arg0.countryDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(12)
    void createTeam_GivenInvalidDTONullCountryDTO_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTONullCountryDTO)
                .post(TEAM_PATH)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country must be chosen", response.getErrors().get("createTeam.arg0.countryDTO"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("createTeam.arg0.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("createTeam.arg0.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.arg0.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("createTeam.arg0.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(13)
    void createTeam_GivenValidDTO_ReturnsCreated() {
        String location = given()
                .contentType(APPLICATION_JSON)
                .body(bocaJuniors)
                .post(TEAM_PATH)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().header("Location");

        Assertions.assertTrue(location.contains(TEAM_PATH + "/1"));
    }


    @Test
    @Order(20)
    void updateTeam_GivenInvalidDTOValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTO)
                .put(TEAM_PATH + "/-1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country name must be between 3 and 20 chars", response.getErrors().get("updateTeam.arg1.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("updateTeam.arg1.countryDTO.code"));
        Assertions.assertEquals("Team founded date must be older than today", response.getErrors().get("updateTeam.arg1.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.arg1.level"));
        Assertions.assertEquals("Team name must be between 3 and 30 chars", response.getErrors().get("updateTeam.arg1.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(21)
    void updateTeam_GivenInvalidDTONullValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTONullValues)
                .put(TEAM_PATH + "/-1")

                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(6, response.getErrors().size());
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("updateTeam.arg1.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("updateTeam.arg1.countryDTO.code"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("updateTeam.arg1.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.arg1.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("updateTeam.arg1.name"));
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("updateTeam.arg1.countryDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(22)
    void updateTeam_GivenInvalidDTONullCountryDTO_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(invalidTeamDTONullCountryDTO)
                .put(TEAM_PATH + "/-1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country must be chosen", response.getErrors().get("updateTeam.arg1.countryDTO"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("updateTeam.arg1.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("updateTeam.arg1.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.arg1.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("updateTeam.arg1.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(23)
    void updateTeam_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(bayernMunchen)
                .put(TEAM_PATH + "/-10000")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find team with id: -10000", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(24)
    void updateTeam_GivenValidDTO_ReturnsOK() {
        TeamDTO response = given()
                .contentType(APPLICATION_JSON)
                .body(bayernMunchen)
                .put(TEAM_PATH + "/-1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().as(TeamDTO.class);

        Assertions.assertEquals(germany.getCode(), response.getCountryDTO().getCode());
        Assertions.assertEquals(germany.getName(), response.getCountryDTO().getName());
        Assertions.assertEquals(bayernMunchen.getFounded(), response.getFounded());
        Assertions.assertEquals(bayernMunchen.getLevel(), response.getLevel());
        Assertions.assertEquals(bayernMunchen.getName(), response.getName());
        Assertions.assertEquals(bayernMunchen.getPicture(), response.getPicture());
        Assertions.assertEquals(bayernMunchen.getNickName(), response.getNickName());
    }

    @Test
    @Order(30)
    void updateTeamLevel_GivenLowerRangeLevelNotAllowed_ReturnsBadRequest() {
        ErrorDTO response = given()
                .patch(TEAM_PATH + "/-1/level/0.9")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeamLevel.arg1"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(31)
    void updateTeamLevel_GivenUpperRangeLevelNotAllowed_ReturnsBadRequest() {
        ErrorDTO response = given()
                .patch(TEAM_PATH + "/-1/level/10.1")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeamLevel.arg1"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(32)
    void updateTeamLevel_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .patch(TEAM_PATH + "/1000000/level/8")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(NOT_FOUND.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find team with id: 1000000", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(33)
    void updateTeamLevel_GivenValidRangeLevel_ReturnsOK() {
        TeamDTO response = given()
                .patch(TEAM_PATH + "/-1/level/8.8")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(OK.getStatusCode())
                .extract().body().as(TeamDTO.class);

        Double level = Double.sum(bayernMunchen.getLevel(), 8.8) / 2;

        Assertions.assertEquals(level, response.getLevel());
    }

    @Test
    @Order(40)
    void deleteTeam_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .delete(TEAM_PATH + "/1000000")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(NOT_FOUND.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find team with id: 1000000", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(41)
    void deleteTeam_GivenValidTeamId_ReturnsNoContent() {
        given()
                .delete(TEAM_PATH + "/-1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(50)
    void findRandomTeam_GivenThereIsNoTeam_ReturnsNotFound() {
        ErrorDTO response = given()
                .when()
                .get(TEAM_PATH + "/random")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find any team", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    private void createBayerMunchenTeam() {
        germany = new CountryDTO("Germany", "DE");
        bayernMunchen = TeamDTO.builder()
                .countryDTO(germany)
                .founded(LocalDate.of(1900, 2, 27))
                .level(9.1d)
                .name("FC Bayern de Munchen")
                .picture("https://storage.googleapis.com/www-paredro-com/uploads/2019/02/%E2%96%B7-Esta-es-la-historia-del-logo-del-Bayern-Mu%CC%81nich-el-gigante-de-Baviera.jpg")
                .nickName("Bayer")
                .build();
    }

    private void createBocaJuniorsTeam() {
        argentina = new CountryDTO("Argentina", "AR");
        bocaJuniors = TeamDTO.builder()
                .countryDTO(argentina)
                .founded(LocalDate.of(1905, 4, 3))
                .level(7.6d)
                .name("Club Atlético Boca Juniors")
                .picture("http://t1.gstatic.com/images?q=tbn:ANd9GcRe-iwuP9o7Fs1FBdqX7s4f8DVKaxfg0ODrOHqMjLTMZ0PkcQ20")
                .nickName("Boca")
                .build();
    }

    private void createInvalidTeamDTO() {
        invalidCountryDTO = new CountryDTO("XX", "XX");
        invalidTeamDTO = TeamDTO.builder()
                .countryDTO(invalidCountryDTO)
                .founded(LocalDate.now())
                .level(11d)
                .name("ss")
                .picture("picture")
                .nickName("nickname")
                .build();
    }

    private void createInvalidTeamDTONullValues() {
        invalidCountryDTONullValues = new CountryDTO(null, null);
        invalidTeamDTONullValues = TeamDTO.builder()
                .countryDTO(invalidCountryDTONullValues)
                .build();
    }

    private void createInvalidTeamDTONullCountryDTO() {
        invalidTeamDTONullCountryDTO = TeamDTO.builder().build();
    }


    private TypeRef<List<TeamDTO>> getTeamDTOTypeRef() {
        return new TypeRef<>() {
            // Kept empty on purpose
        };
    }
}