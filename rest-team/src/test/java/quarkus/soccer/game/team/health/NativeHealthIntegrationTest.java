package quarkus.soccer.game.team.health;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Tag;

@NativeImageTest
@Tag("native")
public class NativeHealthIntegrationTest extends HealthIntegrationTest {
    // Execute the same tests but in native mode.
}
