package quarkus.soccer.game.team.domainobject;

import org.junit.jupiter.api.Test;
import pl.pojo.tester.api.assertion.Method;

import static pl.pojo.tester.api.assertion.Assertions.assertPojoMethodsFor;

class CountryDOTest {

    @Test
    void boilerplate() {
        final Class<?> countryDO = CountryDO.class;

        assertPojoMethodsFor(countryDO)
                .testing(Method.CONSTRUCTOR, Method.GETTER)
                .areWellImplemented();
    }

}