package quarkus.soccer.game.team.datatransferobject;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description="Errors", required = true)
@Getter
@NoArgsConstructor
public class ErrorDTO {

    private Instant timestamp = Instant.now();

    private Map<String, String> errors;

    public ErrorDTO(Map<String, String> errors) {
        this.errors = errors;
    }

}
