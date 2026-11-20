package quarkus.soccer.game.team.dataaccessobject;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import quarkus.soccer.game.team.domainobject.TeamDO;

import javax.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<TeamDO> {

    Random random = new SecureRandom();

    public Optional<TeamDO> findRandomAndDeletedIsFalse() {
        long countTeams = count("deleted = false");
        if (countTeams == 0) {
            return Optional.empty();
        }
        int randomTeam = random.nextInt((int) countTeams);

        return find("deleted = false")
                .page(randomTeam, 1)
                .firstResultOptional();
    }

    // Soft-deleted teams are included on purpose: the uc_team unique constraint also covers them.
    public boolean existsByNameAndCountryCode(String name, String countryCode) {
        return count("name = ?1 and countryDO.code = ?2", name, countryCode) > 0;
    }

    public boolean existsByNameAndCountryCodeAndIdNot(String name, String countryCode, Long id) {
        return count("name = ?1 and countryDO.code = ?2 and id <> ?3", name, countryCode, id) > 0;
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
