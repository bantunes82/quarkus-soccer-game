package quarkus.soccer.game.team.dataaccessobject;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import quarkus.soccer.game.team.domainobject.TeamDO;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<TeamDO> {

    Random random = new Random();

    public Optional<TeamDO> findRandom() {
        long countTeams = count();
        int randomVillain = random.nextInt((int) countTeams);

        return findAll().page(randomVillain, 1).firstResultOptional();
    }

    public Optional<TeamDO> findByNameAndByCountryCode(String name, String countryCode) {
        return find("name = ?1 and countryDO.code = ?2", name, countryCode).firstResultOptional();
    }

    public List<TeamDO> findByName(String name) {
        return find("name", name).list();
    }

    public List<TeamDO> findByCountryCode(String countryCode, int pageIndex, int pageSize) {
        return find("countryDO.code = ?1", Sort.by("name"), countryCode)
                .page(pageIndex, pageSize)
                .list();
    }

}
