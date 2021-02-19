package quarkus.soccer.game.team.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Readiness
@ApplicationScoped
public class ReadinessHealthCheck implements HealthCheck {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oidcAuthServerUrl;

    private Client client = ClientBuilder.newClient();

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Keycloak connections health check");
        if (checkHealthOfOidcAuthServer()) {
            return builder.up()
                    .build();
        } else {
            return builder.down()
                    .withData("reason", "Failed connection")
                    .build();
        }
    }

    private boolean checkHealthOfOidcAuthServer() {
        try {
            Response response = client.target(oidcAuthServerUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            return response.getStatus() == 200;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
