package quarkus.soccer.game.team.datatransferobject;

import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

class TeamDTOTest {

    @Test
    public void boilerplate() {
        final Class<?> teamDTO = TeamDTO.class;

        assertPojoMethodsFor(teamDTO)
                .testing(Method.CONSTRUCTOR, Method.GETTER, Method.SETTER)
                .areWellImplemented();
    }

}