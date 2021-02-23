package quarkus.soccer.game.team.datatransferobject;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.CountryCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description="Country",required = true)
@Getter
@AllArgsConstructor
public class CountryDTO {

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private final String name;

    @CountryCode
    private final String code;

}
