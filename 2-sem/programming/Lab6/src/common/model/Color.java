package common.model;

import common.exception.IncorrectInputException;
import common.util.NumbParser;

public enum Color {
    BLUE(1),
    YELLOW(2),
    ORANGE(3),
    BROWN(4),
    GREEN(5),
    BLACK(6);

    private final int id;
    private static final String message = "\n\tBlue(1), \n\tYellow(2), \n\tOrange(3), \n\tBrown(4), \n\tGreen(5), \n\tBlack(6)";

    Color(int id) {
        this.id = id;
    }

    public static String getColorsInfo() {
        return "NOT NULL! Blue(1), Yellow(2), Orange(3), Brown(4), Green(5), Black(6)";
    }

    public static Color getColor(String input) throws IncorrectInputException {
        if (input == null || input.isBlank()) {
            throw new IncorrectInputException(message);
        }
        String trimmedInput = input.trim();
        try {
            int id = NumbParser.parseInt(input);
            for (Color color : values()) {
                if (color.id == id) return color;
            }
        } catch (NumberFormatException | ArithmeticException ignored) {}
        for (Color color : values()) {
            if (color.name().equalsIgnoreCase(trimmedInput)) {
                return color;
            }
        }
        throw new IncorrectInputException(message);
    }
}