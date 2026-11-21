package quarkus.soccer.game.team.exception;

public class EntityAlreadyExistsException extends Exception {

    public EntityAlreadyExistsException(String message)
    {
        super(message);
    }

}
