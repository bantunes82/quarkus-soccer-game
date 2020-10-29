package quarkus.soccer.game.team.datatransferobject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import quarkus.soccer.game.team.constants.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CountryDTO {

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private String name;

    @NotBlank(message = Validation.COUNTRY_CODE_BLANK)
    @Size(min = 2, max = 2, message = Validation.COUNTRY_CODE_SIZE)
    private String code;

}
