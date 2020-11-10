package quarkus.soccer.game.team;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.core.Application;


@OpenAPIDefinition(
        tags = {
                @Tag(name = "api", description = "Public that can be used by anybody."),
                @Tag(name = "team", description = "Anybody interested in soccer team")
        },
        info = @Info(
                title = "Team Soccer API",
                description = "This API allows CRUD operations on a soccer team",
                version = "1.0",
                contact = @Contact(
                        name = "Bruno Romao Antunes",
                        url = "https://github.com/bantunes82",
                        email = "bantunes82@gmail.com")),
        servers = {@Server(url = "http://localhost:8081"),@Server(url = "http://localhost:8081/rest-team")},
        externalDocs = @ExternalDocumentation(url = "https://github.com/bantunes82/quarkus-soccer-game/tree/main/rest-team")
)
public class TeamApplication extends Application {
}
