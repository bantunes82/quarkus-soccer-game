package quarkus.soccer.game.team.datatransferobject;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Schema(description="Errors", required = true)
@Getter
@NoArgsConstructor
public class ErrorDTO {

    private final Instant timestamp = Instant.now();

    private Map<String, String> errors = new HashMap<>();

    public ErrorDTO(@NonNull Map<String, String> errors) {
        this.errors = errors;
    }

}
