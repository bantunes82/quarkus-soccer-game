package quarkus.soccer.game.team.datatransferobject;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ErrorDTO {

    private Instant timestamp = Instant.now();

    private Map<String, String> errors;

    public ErrorDTO(Map<String, String> errors) {
        this.errors = errors;
    }

}
