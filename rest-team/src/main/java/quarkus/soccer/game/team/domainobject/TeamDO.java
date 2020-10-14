package quarkus.soccer.game.team.domainobject;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import quarkus.soccer.game.team.util.Range;
import quarkus.soccer.game.team.validation.Validation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "team", uniqueConstraints = @UniqueConstraint(name = "uc_team", columnNames = {"name", "country"}))
@Builder
@Getter
@Setter
@ToString
public class TeamDO extends PanacheEntity {

    @NotBlank(message = Validation.TEAM_NAME_BLANK)
    @Size(min = 3, max = 30, message = Validation.TEAM_NAME_SIZE)
    private String name;

    @Column(name = "nick_name")
    private String nickName;

    @NotBlank(message = Validation.TEAM_FOUNDED_BLANK)
    @Past(message = Validation.TEAM_FOUNDED_PAST)
    private LocalDate founded;

    @Range(min = 1.0, max = 10.0)
    private double level;

    @NotBlank(message = Validation.TEAM_PICTURE_BLANK)
    private String picture;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @NotNull(message = Validation.TEAM_COUNTRY_NULL)
    private CountryDO countryDO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamDO teamDO = (TeamDO) o;
        return name.equals(teamDO.name) &&
                countryDO.equals(teamDO.countryDO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countryDO);
    }

}
