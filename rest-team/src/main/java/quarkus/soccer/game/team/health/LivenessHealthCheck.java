package quarkus.soccer.game.team.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessHealthCheck implements HealthCheck {

    @ConfigProperty(name = "quarkus.log.level")
    String QUARKUS_LOG_LEVEL;

    @ConfigProperty(name = "quarkus.log.category.\"quarkus.soccer.game.team\".level")
    String APPLICATION_LOG_LEVEL;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Ping Quarkus Soccer Team API")
                .withData("Property: quarkus.log.level", QUARKUS_LOG_LEVEL)
                .withData("Property: quarkus.log.category.quarkus.soccer.game.team.level", APPLICATION_LOG_LEVEL)
                .up()
                .build();
    }
}
