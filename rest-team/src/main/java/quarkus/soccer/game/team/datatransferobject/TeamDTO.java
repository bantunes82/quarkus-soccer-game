package quarkus.soccer.game.team.datatransferobject;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description="Team Soccer", required = true)
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TeamDTO {

    @NotBlank(message = Validation.TEAM_NAME_BLANK)
    @Size(min = 3, max = 50, message = Validation.TEAM_NAME_SIZE)
    private String name;

    private String nickName;

    @Past(message = Validation.TEAM_FOUNDED_PAST)
    @NotNull(message = Validation.TEAM_FOUNDED_BLANK)
    private LocalDate founded;

    @Range(min = 1.0, max = 10.0)
    private Double level;

    @NotBlank(message = Validation.TEAM_PICTURE_BLANK)
    private String picture;

    @NotNull(message = Validation.TEAM_COUNTRY_NULL)
    @Valid
    private CountryDTO countryDTO;

}
