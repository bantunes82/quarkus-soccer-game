package quarkus.soccer.game.team.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import quarkus.soccer.game.team.container.DatabaseResource;
import quarkus.soccer.game.team.container.IdentityAccessManagementResource;
import quarkus.soccer.game.team.datatransferobject.CountryDTO;
import quarkus.soccer.game.team.datatransferobject.ErrorDTO;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@QuarkusTest
@TestHTTPEndpoint(TeamResource.class)
@QuarkusTestResource(DatabaseResource.class)
@QuarkusTestResource(IdentityAccessManagementResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
class TeamResourceIntegrationTest {

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

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    private String authServerUrl;
    @ConfigProperty(name = "quarkus.oidc.client-id")
    private String clientId;

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
                .get("/random")
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
                .get("/name/XXXXX")
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
                .get("/name/Sport Club Corinthians Paulista")
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
                .get("/country/AR")
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
                .get("/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("findTeamByCountryCode.countryCode"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(6)
    @Disabled("Disabled until we check why internationalization is not working properly")
    void findTeamByCountryCode_GivenInvalidCountryCode_ReturnsBadRequestSpanish() {
        ErrorDTO response = given()
                .when()
                .header(ACCEPT_LANGUAGE, "es")
                .get("/country/XX")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Codigo del país debe tener 2 caracteres, ser un código existente y en Mayúsculo", response.getErrors().get("findTeamByCountryCode.countryCode"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(7)
    void findTeamByCountryCode_GivenExistentTeamWithTheCountryCode_ReturnsTeamListWithOneElement() {
        List<TeamDTO> response = given()
                .when()
                .get("/country/BR")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getTeamDTOTypeRef());

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(TEAM_NAME, response.get(0).getName());
    }

    @Test
    @Order(20)
    void updateTeam_GivenInvalidDTOValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTO)
                .put("/-1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country name must be between 3 and 20 chars", response.getErrors().get("updateTeam.teamDTO.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("updateTeam.teamDTO.countryDTO.code"));
        Assertions.assertEquals("Team founded date must be older than today", response.getErrors().get("updateTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be between 3 and 50 chars", response.getErrors().get("updateTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(21)
    void updateTeam_GivenInvalidDTONullValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTONullValues)
                .put("/-1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(6, response.getErrors().size());
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("updateTeam.teamDTO.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("updateTeam.teamDTO.countryDTO.code"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("updateTeam.teamDTO.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("updateTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("updateTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(22)
    void updateTeam_GivenInvalidDTONullCountryDTO_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTONullCountryDTO)
                .put("/-1")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country must be chosen", response.getErrors().get("updateTeam.teamDTO.countryDTO"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("updateTeam.teamDTO.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("updateTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("updateTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(23)
    void updateTeam_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(bayernMunchen)
                .put("/-10000")
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
    void updateTeam_GivenValidDTOAndAllowedRoleUser_ReturnsOK() {
        TeamDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(bayernMunchen)
                .put("/-1")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(TeamDTO.class);

        Assertions.assertEquals(germany.getCode(), response.getCountryDTO().getCode());
        Assertions.assertEquals(germany.getName(), response.getCountryDTO().getName());
        Assertions.assertEquals(bayernMunchen.getFounded(), response.getFounded());
        Assertions.assertEquals(8.55, response.getLevel());
        Assertions.assertEquals(bayernMunchen.getName(), response.getName());
        Assertions.assertEquals(bayernMunchen.getPicture(), response.getPicture());
        Assertions.assertEquals(bayernMunchen.getNickName(), response.getNickName());
    }

    @Test
    @Order(25)
    void updateTeam_GivenValidDTOAndNotAllowedRoleUser_ReturnsForbidden() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForNotAllowedRoleUser())
                .body(bayernMunchen)
                .put("/-1")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @Order(26)
    void updateTeam_GivenValidDTOAndInvalidAccessToken_ReturnsUnauthorized() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2("XX")
                .body(bayernMunchen)
                .put("/-1")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }


    @Test
    @Order(30)
    void updateTeamLevel_GivenLowerRangeLevelNotAllowed_ReturnsBadRequest() {
        ErrorDTO response = given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .patch("/-1/level/0.9")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeamLevel.level"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(31)
    void updateTeamLevel_GivenUpperRangeLevelNotAllowed_ReturnsBadRequest() {
        ErrorDTO response = given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .patch("/-1/level/10.1")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("updateTeamLevel.level"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(32)
    void updateTeamLevel_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .patch("/1000000/level/8")
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
    void updateTeamLevel_GivenValidRangeLevelAndAllowedRoleUser_ReturnsOK() {
        TeamDTO response = given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .patch("/-1/level/8.8")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(OK.getStatusCode())
                .extract().body().as(TeamDTO.class);

        Assertions.assertEquals(8.675, response.getLevel());
    }

    @Test
    @Order(34)
    void updateTeamLevel_GivenValidRangeLevelAndNotAllowedRoleUser_ReturnsForbidden() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForNotAllowedRoleUser())
                .patch("/-1/level/8.8")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @Order(35)
    void updateTeamLevel_GivenValidRangeLevelAndInvalidAccessToken_ReturnsUnauthorized() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2("XX")
                .patch("/-1/level/8.8")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    @Order(40)
    void deleteTeam_GivenInvalidTeamId_ReturnsNotFound() {
        ErrorDTO response = given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .delete("/1000000")
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
    void deleteTeam_GivenValidTeamIdAndAllowedRoleUser_ReturnsNoContent() {
        given()
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .delete("/-1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(42)
    void deleteTeam_GivenValidTeamIdAndNotAllowedRoleUser_ReturnsForbidden() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForNotAllowedRoleUser())
                .delete("/-1")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @Order(43)
    void deleteTeam_GivenValidTeamIdAndInvalidAccessToken_ReturnsUnauthorized() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2("XX")
                .delete("/-1")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    @Order(50)
    void findRandomTeam_GivenThereIsNoTeam_ReturnsNotFound() {
        ErrorDTO response = given()
                .when()
                .get("/random")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Could not find any team", response.getErrors().get("error; "));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(60)
    void createTeam_GivenInvalidDTOValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country name must be between 3 and 20 chars", response.getErrors().get("createTeam.teamDTO.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("createTeam.teamDTO.countryDTO.code"));
        Assertions.assertEquals("Team founded date must be older than today", response.getErrors().get("createTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be between 3 and 50 chars", response.getErrors().get("createTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(61)
    void createTeam_GivenInvalidDTONullValues_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTONullValues)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(6, response.getErrors().size());
        Assertions.assertEquals("Country name must be filled", response.getErrors().get("createTeam.teamDTO.countryDTO.name"));
        Assertions.assertEquals("Country code must have 2 chars, be a existent code and Uppercase", response.getErrors().get("createTeam.teamDTO.countryDTO.code"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("createTeam.teamDTO.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("createTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("createTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(62)
    void createTeam_GivenInvalidDTONullCountryDTO_ReturnsBadRequest() {
        ErrorDTO response = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(invalidTeamDTONullCountryDTO)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(ErrorDTO.class);

        Assertions.assertEquals(5, response.getErrors().size());
        Assertions.assertEquals("Country must be chosen", response.getErrors().get("createTeam.teamDTO.countryDTO"));
        Assertions.assertEquals("Team picture must be filled", response.getErrors().get("createTeam.teamDTO.picture"));
        Assertions.assertEquals("Team founded must be filled", response.getErrors().get("createTeam.teamDTO.founded"));
        Assertions.assertEquals("Team Level must be between 1 and 10", response.getErrors().get("createTeam.teamDTO.level"));
        Assertions.assertEquals("Team name must be filled", response.getErrors().get("createTeam.teamDTO.name"));
        Assertions.assertNotNull(response.getTimestamp());
    }

    @Test
    @Order(63)
    void createTeam_GivenValidDTOAndAllowedRoleUser_ReturnsCreated() {
        String location = given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForAllowedRoleUser())
                .body(bocaJuniors)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().header(LOCATION);

        Assertions.assertTrue(location.contains("/v1/teams"));
    }

    @Test
    @Order(64)
    void createTeam_GivenValidDTOAndNotAllowedRoleUser_ReturnsForbidden() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2(getAccessTokenForNotAllowedRoleUser())
                .body(bocaJuniors)
                .post()
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @Order(65)
    void createTeam_GivenValidDTOAndInvalidAccessToken_ReturnsUnauthorized() {
        given()
                .contentType(APPLICATION_JSON)
                .auth().oauth2("XX")
                .body(bocaJuniors)
                .post()
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
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

    private String getAccessToken(String username, String password) {
        Map<String, String> attributes = new HashMap();
        attributes.put("grant_type", "password");
        attributes.put("client_id", clientId);
        attributes.put("client_secret", "6fe5572d-d0f7-4121-8fc4-d2768bf82836");
        attributes.put("username", username);
        attributes.put("password", password);

        String accessToken = given()
                .contentType(APPLICATION_FORM_URLENCODED)
                .formParams(attributes)
                .post(authServerUrl.concat("/protocol/openid-connect/token"))
                .then()
                .statusCode(OK.getStatusCode())
                .extract().body().jsonPath().getString("access_token");

        return accessToken;
    }

    private String getAccessTokenForAllowedRoleUser() {
        return getAccessToken("teamuser", "teamuser");
    }

    private String getAccessTokenForNotAllowedRoleUser() {
        return getAccessToken("test", "test");
    }

}