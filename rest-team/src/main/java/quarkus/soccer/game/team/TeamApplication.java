package quarkus.soccer.game.team;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.ws.rs.core.Application;


@OpenAPIDefinition(
        info = @Info(
                title = "Team Soccer API",
                description = "This API allows CRUD operations on a soccer team",
                version = "1.0",
                contact = @Contact(
                        name = "Bruno Romao Antunes",
                        url = "https://github.com/bantunes82",
                        email = "bantunes82@gmail.com")),
        externalDocs = @ExternalDocumentation(url = "https://github.com/bantunes82/quarkus-soccer-game/tree/main/rest-team")
)
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "accessToken",
                type = SecuritySchemeType.HTTP,
                description = "Access token for the user that belongs to TEAM role",
                scheme = "Bearer")}
)
public class TeamApplication extends Application {
}
