package quarkus.soccer.game.team.datatransferobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.CountryCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private String name;

    @CountryCode
    private String code;

}
