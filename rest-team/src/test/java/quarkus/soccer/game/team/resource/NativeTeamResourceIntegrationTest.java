package quarkus.soccer.game.team.resource;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Tag;

@NativeImageTest
@Tag("native")
public class NativeTeamResourceIntegrationTest extends TeamResourceIntegrationTest {
    // Execute the same tests but in native mode.
}
