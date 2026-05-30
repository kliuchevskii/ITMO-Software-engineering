package common.exception;

import java.io.File;

public class RecursionException extends RuntimeException {
    public RecursionException(File file) {
        super("Выявлена рекурсия, выполнение файла " + file + " будет остановлено.\n");
    }
}