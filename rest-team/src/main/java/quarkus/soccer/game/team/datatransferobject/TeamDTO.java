package quarkus.soccer.game.team.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    @NotBlank(message = Validation.TEAM_NAME_BLANK)
    @Size(min = 3, max = 30, message = Validation.TEAM_NAME_SIZE)
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
