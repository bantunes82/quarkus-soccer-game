package quarkus.soccer.game.team.health;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@Tag("integration")
class HealthIntegrationTest {


    @Test
    void shouldPingLiveness() {
        given()
                .get("/health/live")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body(containsString("Ping Quarkus Soccer Team API"))
                .body(containsString("Property: quarkus.log.level"))
                .body(containsString("Property: quarkus.log.category.quarkus.soccer.game.team.level"))
                .body(containsString("UP"));
    }

    @Test
    void shouldPingReadiness() {
        given()
                .get("/health/ready")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body(containsString("Database connections health check"))
                .body(containsString("Keycloak connections health check"))
                .body(containsString("UP"));
    }

    @Test
    void shouldPingHealth(){
        given()
                .get("/health")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body(containsString("Ping Quarkus Soccer Team API"))
                .body(containsString("Property: quarkus.log.level"))
                .body(containsString("Property: quarkus.log.category.quarkus.soccer.game.team.level"))
                .body(containsString("Database connections health check"))
                .body(containsString("Keycloak connections health check"))
                .body(containsString("UP"));
    }
}
