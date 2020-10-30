package quarkus.soccer.game.team.datatransferobject;

import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

class ErrorDTOTest {

    @Test
    public void boilerplate() {
        final Class<?> errorDTO = ErrorDTO.class;

        assertPojoMethodsFor(errorDTO)
                .testing(Method.GETTER)
                .areWellImplemented();
    }

}