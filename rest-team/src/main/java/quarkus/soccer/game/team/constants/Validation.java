package quarkus.soccer.game.team.constants;

public class Validation {
    public static final String TEAM_NAME_BLANK = "{constraint.team.nameNotBlank}";;
    public static final String TEAM_NAME_SIZE =  "{constraint.team.nameSize}";
    public static final String TEAM_FOUNDED_BLANK = "{constraint.team.foundedNotBlank}";
    public static final String TEAM_FOUNDED_PAST = "{constraint.team.foundedPast}";
    public static final String TEAM_PICTURE_BLANK = "{constraint.team.pictureNotBlank}";
    public static final String TEAM_LEVEL_INVALID = "{constraint.team.levelInvalid}";
    public static final String TEAM_COUNTRY_NULL = "{constraint.team.countryNotNull}";

    public static final String COUNTRY_NAME_BLANK = "{constraint.country.nameNotBlank}";
    public static final String COUNTRY_CODE_BLANK =  "{constraint.country.codeNotBlank}";
    public static final String COUNTRY_NAME_SIZE =  "{constraint.country.nameSize}";
    public static final String COUNTRY_CODE_SIZE = "{constraint.country.codeSize}";
}