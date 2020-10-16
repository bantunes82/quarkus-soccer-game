package quarkus.soccer.game.team.domainobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quarkus.soccer.game.team.constants.Validation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "country", uniqueConstraints = {
        @UniqueConstraint(name = "uc_country_name", columnNames = {"name"}),
        @UniqueConstraint(name = "uc_country_code", columnNames = {"code"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryDO {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private String name;

    @NotBlank(message = Validation.COUNTRY_CODE_BLANK)
    @Size(min = 2, max = 3, message = Validation.COUNTRY_CODE_SIZE)
    private String code;


}
