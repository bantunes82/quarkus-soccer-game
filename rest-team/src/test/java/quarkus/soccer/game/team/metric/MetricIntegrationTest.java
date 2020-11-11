package quarkus.soccer.game.team.metric;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@Tag("integration")
public class MetricIntegrationTest {

    @Test
    void shouldPingMetrics() {
        given()
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .get("/metrics/application")
                .then()
                .contentType(APPLICATION_JSON)
                .statusCode(OK.getStatusCode())
                .body(containsString("countFindRandomTeam"))
                .body(containsString("timeFindRandomTeam"))
                .body(containsString("countFindTeamByName"))
                .body(containsString("timeFindTeamByName"))
                .body(containsString("countFindTeamByCountryCode"))
                .body(containsString("timeFindTeamByCountryCode"))
                .body(containsString("countCreateTeam"))
                .body(containsString("timeCreateTeam"))
                .body(containsString("countUpdateTeam"))
                .body(containsString("timeUpdateTeam"))
                .body(containsString("countUpdateTeamLevel"))
                .body(containsString("timeUpdateTeamLevel"))
                .body(containsString("countDeleteTeam"))
                .body(containsString("timeDeleteTeam"));
    }
}
