package quarkus.soccer.game.team.service;

import quarkus.soccer.game.team.dataaccessobject.CountryRepository;
import quarkus.soccer.game.team.dataaccessobject.TeamRepository;
import quarkus.soccer.game.team.domainobject.CountryDO;
import quarkus.soccer.game.team.domainobject.TeamDO;
import quarkus.soccer.game.team.exception.EntityNotFoundException;
import quarkus.soccer.game.team.util.Range;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class TeamService {

    private TeamRepository teamRepository;
    private CountryRepository countryRepository;

    @Inject
    public TeamService(TeamRepository teamRepository, CountryRepository countryRepository) {
        this.teamRepository = teamRepository;
        this.countryRepository = countryRepository;
    }

    public TeamDO findRandom() throws EntityNotFoundException {
        return teamRepository.findRandomAndDeletedIsFalse().orElseThrow(() -> new EntityNotFoundException("Could not find any team"));
    }

    public List<TeamDO> findByName(String name) {
        return teamRepository.findByNameAndDeletedIsFalse(name);
    }

    public List<TeamDO> findByCountryCode(String countryCode, int pageIndex, int pageSize) {
        return teamRepository.findByCountryCodeAndDeletedIsFalse(countryCode, pageIndex, pageSize);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public TeamDO create(@Valid TeamDO team) {
        Optional<CountryDO> countryDO = countryRepository.findByCountryCode(team.getCountryDO().getCode());
        countryDO.ifPresent(team::setCountryDO);
        teamRepository.persist(team);

        return team;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public TeamDO update(Long teamId, @Valid TeamDO team) throws EntityNotFoundException {
        TeamDO teamSaved = findTeamChecked(teamId);

        team.setId(teamSaved.getId());

        return create(team);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public TeamDO updateLevel(Long teamId,@Valid @Range(min = 1.0, max = 10.0)Double level) throws EntityNotFoundException {
        TeamDO teamSaved = findTeamChecked(teamId);
        final Double newLevel = Double.sum(teamSaved.getLevel(),level)/2;

        teamSaved.setLevel(newLevel);

        return teamSaved;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(Long teamId) throws EntityNotFoundException {
        TeamDO teamDO = findTeamChecked(teamId);
        teamDO.setDeleted(true);
    }


    private TeamDO findTeamChecked(Long teamId) throws EntityNotFoundException {
        return teamRepository.findByIdOptional(teamId).orElseThrow(() -> new EntityNotFoundException("Could not find team with id: " + teamId));
    }


}
