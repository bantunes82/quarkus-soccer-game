package quarkus.soccer.game.team.domainobject;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import quarkus.soccer.game.team.validation.Validation;

import javax.persistence.Entity;
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
@EqualsAndHashCode(callSuper = false)
public class CountryDO extends PanacheEntity {

    @NotBlank(message = Validation.COUNTRY_NAME_BLANK)
    @Size(min = 3, max = 20, message = Validation.COUNTRY_NAME_SIZE)
    private String name;

    @NotBlank(message = Validation.COUNTRY_CODE_BLANK)
    @Size(min = 2, max = 3,  message = Validation.COUNTRY_CODE_SIZE)
    private String code;


}
