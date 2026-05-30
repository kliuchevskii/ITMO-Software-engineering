package common.model;

import common.exception.IncorrectInputException;
import common.util.NumbParser;

public enum Country {
    RUSSIA(1),
    UNITED_KINGDOM(2),
    GERMANY(3),
    SPAIN(4);

    private final int id;
    private static final String message = "\n\tRussia(1), \n\tUnited_Kingdom(2), \n\tGermany(3), \n\tSpain(4), \n\tNull";

    Country(int id) {
        this.id = id;
    }

    public static String getNationalityInfo(){
        return "NOT NULL! Russia(1), United_Kingdom(2), Germany(3), Spain(4), Null";
    }

    public static Country getCountry(String input) throws IncorrectInputException {
        if (input == null || input.isBlank()) {
            throw new IncorrectInputException(message);
        }
        String trimmedInput = input.trim();
        try {
            int id = NumbParser.parseInt(input);
            for (Country country : values()) {
                if (country.id == id) return country;
            }
        } catch (NumberFormatException | ArithmeticException ignored) {}
        for (Country country : values()) {
            if (country.name().equalsIgnoreCase(trimmedInput)) {
                return country;
            }
        }
        throw new IncorrectInputException(message);
    }
}