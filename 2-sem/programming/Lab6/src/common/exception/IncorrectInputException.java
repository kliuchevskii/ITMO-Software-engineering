package common.exception;



/**
 * Исключение для некорректного ввода данных.
 */
public class IncorrectInputException extends RuntimeException {
    public IncorrectInputException(String conditions) {
        super(format(conditions));
    }

    private static String format(String cond) {
        if (cond == null || cond.isBlank()) {
            return "Ваш ввод некорректен. Попробуйте еще раз: ";
        }
        if (!cond.contains(",")) {
            return "\nДопустимое значение параметра: " + cond.trim() + ".\nПопробуйте еще раз: ";
        }
        String list = java.util.Arrays.stream(cond.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.joining("\n\t", "\n\t", ""));
        return "\nДопустимые значения параметра: " + list + "\nПопробуйте еще раз: ";
    }
}