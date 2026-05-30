package common.exception;

public class ExecuteException extends RuntimeException {
    public ExecuteException(String message) {
        super("Ошибка исполнения скрипта. Команда " + message + " была прервана.\n");
    }
}