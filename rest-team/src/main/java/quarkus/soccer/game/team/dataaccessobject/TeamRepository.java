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

    public Optional<TeamDO> findRandomAndDeletedIsFalse() {
        long countTeams = count();
        int randomVillain = count() == 0 ? 1 : random.nextInt((int) countTeams);

        return find("deleted = false")
                .page(randomVillain, 1)
                .firstResultOptional();
    }

    public Optional<TeamDO> findByIdAndDeletedIsFalse(Long id) {
        return find("id = ?1 and deleted = false", id).firstResultOptional();
    }

    public List<TeamDO> findByNameAndDeletedIsFalse(String name) {
        return find("name = ?1 and deleted = false", name).list();
    }

    public List<TeamDO> findByCountryCodeAndDeletedIsFalse(String countryCode, int pageIndex, int pageSize) {
        return find("countryDO.code = ?1 and deleted = false", Sort.by("name"), countryCode)
                .page(pageIndex, pageSize)
                .list();
    }

}
