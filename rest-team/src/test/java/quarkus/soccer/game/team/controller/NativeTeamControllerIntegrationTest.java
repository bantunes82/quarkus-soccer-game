package quarkus.soccer.game.team.controller;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Tag;

@NativeImageTest
@Tag("native")
public class NativeTeamControllerIntegrationTest extends TeamControllerIntegrationTest {
    // Execute the same tests but in native mode.
}
