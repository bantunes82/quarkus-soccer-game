package quarkus.soccer.game.team.domainobject;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uc_team", columnNames = {"name", "country"}))
@Data
public class Team extends PanacheEntity {

    @NotNull
    @Size(min = 3, max = 30)
    private String name;

    private String nickName;

    @NotNull
    private LocalDate founded;

    @Min(1) @Max(10)
    private int level;

    @NotNull
    private String picture;

    @OneToMany
    @NotNull
    private Country country;



}
