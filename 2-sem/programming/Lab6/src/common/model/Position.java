package common.model;

import common.exception.IncorrectInputException;
import common.util.NumbParser;

public enum Position {
    HUMAN_RESOURCES(1),
    HEAD_OF_DEPARTMENT(2),
    LEAD_DEVELOPER(3),
    MANAGER_OF_CLEANING(4);

    private final int id;
    private static final String MESSAGE = "\n\tHuman_Resources(1), \n\tHead_of_department(2), \n\tLead_developer(3), \n\tManager_of_cleaning(4)";

    Position(int id) {
        this.id = id;
    }

    public static String getNationalityInfo() {
        return "NOT NULL! Human_Resources(1), Head_of_department(2), Lead_developer(3), Manager_of_cleaning(4)";
    }

    public static Position getPosition(String input) throws IncorrectInputException {
        if (input == null || input.isBlank()) {
            throw new IncorrectInputException(MESSAGE);
        }
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            int id = NumbParser.parseInt(trimmedInput);
            for (Position position : values()) {
                if (position.id == id) return position;
            }
        } catch (NumberFormatException | ArithmeticException ignored) {}
        for (Position position : values()) {
            if (position.name().equalsIgnoreCase(trimmedInput)) {
                return position;
            }
        }
        throw new IncorrectInputException(MESSAGE);
    }

    public int getId() { return id; }
}