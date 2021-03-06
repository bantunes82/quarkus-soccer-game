package quarkus.soccer.game.team.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.commons.collections4.map.HashedMap;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Map;

public class IdentityAccessManagementResource implements QuarkusTestResourceLifecycleManager {

    private static final GenericContainer IDENTITY_ACCESS_MANAGEMENT = new GenericContainer("quay.io/keycloak/keycloak:12.0.4")
            .withCommand("-b 0.0.0.0 -Djboss.http.port=8082 -Dkeycloak.profile.feature.upload_scripts=enabled -Dkeycloak.migration.action=import " +
                    "-Dkeycloak.migration.provider=dir -Dkeycloak.migration.dir=/tmp/keycloak/realms -Dkeycloak.migration.strategy=OVERWRITE_EXISTING")
            .withClasspathResourceMapping("./keycloak/realms", "/tmp/keycloak/realms/", BindMode.READ_ONLY)
            .withStartupTimeout(Duration.ofSeconds(120))
            .withExposedPorts(8082)
            .withEnv("DB_VENDOR", "h2")
            .waitingFor(Wait.forHttp("/auth"));

    @Override
    public Map<String, String> start() {
        IDENTITY_ACCESS_MANAGEMENT.start();

        String authServerUrl = String.format("http://%s:%d/auth/realms/team-realm", IDENTITY_ACCESS_MANAGEMENT.getHost(),IDENTITY_ACCESS_MANAGEMENT.getMappedPort(8082));

        Map<String, String> properties = new HashedMap<>();
        properties.put("quarkus.oidc.auth-server-url", authServerUrl);
        properties.put("quarkus.oidc.client-id", "team-client");

        return properties;
    }

    @Override
    public void stop() {
        IDENTITY_ACCESS_MANAGEMENT.stop();
    }
}
