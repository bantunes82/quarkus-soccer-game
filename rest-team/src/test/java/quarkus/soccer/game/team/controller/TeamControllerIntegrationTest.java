package quarkus.soccer.game.team.controller;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quarkus.soccer.game.team.container.DatabaseResource;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestTransaction
class TeamControllerIntegrationTest {

    private String TEAM_PATH = "/v1/teams";


    @BeforeEach
    void setUp() {
    }

    @Test
    void findRandomTeam_GivenThereIsNoTeam_ReturnsNotFound() {
        given().delete(TEAM_PATH + "/1").then().statusCode(NO_CONTENT.getStatusCode());

        ErrorDTO response = given()
                .when()
                .get(TEAM_PATH + "/random")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find any team", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    void findRandomTeam_GivenThereIsTeam_ReturnsOK() {
        TeamDTO response = given()
                .when()
                .get(TEAM_PATH + "/random")
                .then()
                .statusCode(OK.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(TeamDTO.class);

        Assertions.assertEquals("Sport Club Corinthians Paulista", response.getName());

    }

    @Test
    void findTeamByName_GivenNoExistentTeamName_ReturnsEmptyTeamList() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/name/XXXXX")
                .then()
                .statusCode(OK.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(0, response.size());
    }

    @Test
    void findTeamByName_GivenExistentTeamName_ReturnsTeamListWithOneElement() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/name/Sport Club Corinthians Paulista")
                .then()
                .statusCode(OK.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Sport Club Corinthians Paulista", response.get(0).getName());
    }

    @Test
    void findTeamByCountryCode_GivenNoExistentTeamWithTheCountryCode_ReturnsEmptyTeamList() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/country/AR")
                .then()
                .statusCode(OK.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(0, response.size());
    }

    @Test
    void findTeamByCountryCode_GivenInvalidCountryCode_ReturnsBadRequest() {
        ErrorDTO response = given()
                .when()
                .get(TEAM_PATH + "/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("arg0"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    void findTeamByCountryCode_GivenInvalidCountryCode_ReturnsBadRequestSpanish() {
        ErrorDTO response = given()
                .when()
                .header(ACCEPT_LANGUAGE,"es")
                .get(TEAM_PATH + "/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Codigo del país debe tener 2 caracteres, ser un código existente y en Mayúsculo", response.getErrors().get("arg0"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    void findTeamByCountryCode_GivenExistentTeamWithTheCountryCode_ReturnsTeamListWithOneElement() {
        List<TeamDTO> response = given()
                .when()
                .get(TEAM_PATH + "/country/BR")
                .then()
                .statusCode(OK.getStatusCode())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Sport Club Corinthians Paulista", response.get(0).getName());
    }

    @Test
    void createTeam() {
    }

    @Test
    void updateTeam() {
    }

    @Test
    void updateTeamLevel() {
    }

    @Test
    void deleteTeam() {
    }

    private TypeRef<List<TeamDTO>> getTeamDTOTypeRef() {
        return new TypeRef<List<TeamDTO>>() {
            // Kept empty on purpose
        };
    }
}