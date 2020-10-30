package quarkus.soccer.game.team.controller.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quarkus.soccer.game.team.datatransferobject.CountryDTO;
import quarkus.soccer.game.team.domainobject.CountryDO;

class CountryMapperTest {

    private CountryMapper countryMapper;
    private CountryDO countryDO;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {
        countryMapper = new CountryMapperImpl();

        countryDO = new CountryDO(1L, "Brasil", "BR");
        countryDTO = new CountryDTO("Brasil", "BR");
    }

    @Test
    void toCountryDO_ReturnsCountryDO() {
        CountryDO result = countryMapper.toCountryDO(countryDTO);

        Assertions.assertEquals(countryDTO.getName(), result.getName());
        Assertions.assertEquals(countryDTO.getCode(), result.getCode());
    }

    @Test
    void toCountryDTO_ReturnsCountryDTO() {
        CountryDTO result = countryMapper.toCountryDTO(countryDO);

        Assertions.assertEquals(countryDO.getName(), result.getName());
        Assertions.assertEquals(countryDO.getCode(), result.getCode());
    }
}