package quarkus.soccer.game.team.controller.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import quarkus.soccer.game.team.datatransferobject.TeamDTO;
import quarkus.soccer.game.team.domainobject.TeamDO;

import java.util.List;

@Mapper(config = QuarkusMappingConfig.class, uses = {CountryMapper.class})
public interface TeamMapper {

    @Mapping(source = "countryDTO", target = "countryDO")
    TeamDO toTeamDO(TeamDTO teamDTO);

    @InheritInverseConfiguration
    TeamDTO toTeamDTO(TeamDO teamDO);

    List<TeamDTO> toTeamDTOList(List<TeamDO> teamDOs);

}
