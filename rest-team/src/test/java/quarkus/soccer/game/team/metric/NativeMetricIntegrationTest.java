package quarkus.soccer.game.team.metric;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Tag;

@NativeImageTest
@Tag("native")
public class NativeMetricIntegrationTest extends MetricIntegrationTest {
    // Execute the same tests but in native mode.
}
