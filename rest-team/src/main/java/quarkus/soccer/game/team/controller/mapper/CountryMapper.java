package quarkus.soccer.game.team.controller.mapper;

import org.mapstruct.Mapper;
import quarkus.soccer.game.team.datatransferobject.CountryDTO;
import quarkus.soccer.game.team.domainobject.CountryDO;

@Mapper(config = QuarkusMappingConfig.class)
public interface CountryMapper {

    CountryDO toCountryDO(CountryDTO countryDTO);

    CountryDTO toCountryDTO(CountryDO countryDO);

}
