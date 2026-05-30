package common.model;

import common.exception.IncorrectInputException;
import common.util.NumbParser;

public enum Status {
    FIRED(1),
    HIRED(2),
    RECOMMENDED_FOR_PROMOTION(3),
    REGULAR(4);

    private final int id;
    private static final String message = "\n\tFired(1), \n\tHired(2), \n\tRecommended_for_promotion(3), \n\tRegular(4)";

    Status(int id) {
        this.id = id;
    }

    public static String getNationalityInfo(){
        return "NOT NULL! Fired(1), Hired(2), Recommended_for_promotion(3), Regular(4)";
    }

    public static Status getStatus(String input) throws IncorrectInputException {
        if (input == null || input.isBlank()) {
            throw new IncorrectInputException(message);
        }
        String trimmedInput = input.trim();
        try {
            int id = NumbParser.parseInt(input);
            for (Status status : values()) {
                if (status.id == id) return status;
            }
        } catch (NumberFormatException | ArithmeticException ignored) {}
        for (Status status : values()) {
            if (status.name().equalsIgnoreCase(trimmedInput)) {
                return status;
            }
        }
        throw new IncorrectInputException(message);
    }

    public int getId() { return id; }
}
