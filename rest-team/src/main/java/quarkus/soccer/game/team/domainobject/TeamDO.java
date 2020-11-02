package quarkus.soccer.game.team.domainobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import quarkus.soccer.game.team.constants.Validation;
import quarkus.soccer.game.team.util.Range;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "team", uniqueConstraints = @UniqueConstraint(name = "uc_team", columnNames = {"name", "country"}))
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDO {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = Validation.TEAM_NAME_BLANK)
    @Size(min = 3, max = 50, message = Validation.TEAM_NAME_SIZE)
    private String name;

    @Column(name = "nick_name")
    private String nickName;

    @Past(message = Validation.TEAM_FOUNDED_PAST)
    @NotNull(message = Validation.TEAM_FOUNDED_BLANK)
    private LocalDate founded;

    @Range(min = 1.0, max = 10.0)
    private Double level;

    @NotBlank(message = Validation.TEAM_PICTURE_BLANK)
    private String picture;

    @Column(nullable = false)
    private Boolean deleted = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @NotNull(message = Validation.TEAM_COUNTRY_NULL)
    @JoinColumn(name = "country", nullable = false)
    @Valid
    private CountryDO countryDO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamDO teamDO = (TeamDO) o;
        return id.equals(teamDO.id) &&
                name.equals(teamDO.name) &&
                countryDO.equals(teamDO.countryDO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, countryDO);
    }

}
