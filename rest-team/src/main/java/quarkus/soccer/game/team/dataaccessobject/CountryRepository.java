package quarkus.soccer.game.team.dataaccessobject;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import quarkus.soccer.game.team.domainobject.CountryDO;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CountryRepository implements PanacheRepository<CountryDO> {

    public Optional<CountryDO> findByCountryCode(String countryCode) {
        return find("code = ?1", countryCode).firstResultOptional();
    }
}
