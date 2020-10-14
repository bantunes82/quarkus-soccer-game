package quarkus.soccer.game.team.domainobject;


import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

class TeamDOTest {

    private final String teamName = "Sport Club Corinthians Paulista";
    private final CountryDO countryDO = new CountryDO("Brasil", "BR");

    private final TeamDO team1 = TeamDO.builder()
            .name(teamName)
            .nickName("Timao")
            .founded(LocalDate.of(1910, 9, 1))
            .level(9)
            .picture("https://conteudo.imguol.com.br//c/esporte/futebol/times/desktop/corinthians.jpg")
            .countryDO(countryDO)
            .build();

    private final TeamDO team2 = TeamDO.builder()
            .name(teamName)
            .nickName("Time do Povo")
            .founded(LocalDate.of(1910, 9, 2))
            .level(8)
            .picture("https://conteudo.imguol.com.br//c/esporte/futebol/times/desktop/cruzeiro.jpg")
            .countryDO(countryDO)
            .build();


    @Test
    public void boilerplate() {
        final Class<?> teamDO = TeamDO.class;

        assertPojoMethodsFor(teamDO)
                .testing(Method.CONSTRUCTOR, Method.TO_STRING, Method.GETTER, Method.SETTER)
                .areWellImplemented();
    }

    @Test
    public void equals_Custom() {
        assertEquals(team1, team2);
    }


    @Test
    public void equals_CustomWithNameDifferent() {
        team2.setName("Cruzeiro");
        assertNotEquals(team1, team2);
    }


    @Test
    public void equals_CustomWithCountryDifferent() {
        team2.setCountryDO(new CountryDO("Argentina", "AR"));
        assertNotEquals(team1, team2);
    }

    @Test
    public void equals_CustomDifferentObjects() {
        assertNotEquals(team1, new Object());
    }
}