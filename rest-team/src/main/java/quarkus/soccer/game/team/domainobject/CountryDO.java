package quarkus.soccer.game.team.domainobject;

import lombok.*;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.CountryCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "country", uniqueConstraints = {
        @UniqueConstraint(name = "uc_country_code", columnNames = {"code"})
})
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CountryDO {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private String name;

    @CountryCode
    private String code;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryDO countryDO = (CountryDO) o;
        return code.equals(countryDO.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
