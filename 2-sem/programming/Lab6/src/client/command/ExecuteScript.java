package client.command;

import client.Client;
import client.ClientCommandReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScript extends Command {
    private final ClientCommandReader commandReader;
    private static final Set<File> scriptStack = new HashSet<>();

    public ExecuteScript(Client client, ClientCommandReader commandReader) {
        super(client);
        this.commandReader = commandReader;
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        String filePath = input.trim();
        if (filePath.isEmpty()) {
            System.out.println("Не указан путь к файлу скрипта.");
            return;
        }

        File scriptFile = new File(filePath);
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            System.out.println("Файл не найден: " + scriptFile.getAbsolutePath());
            return;
        }

        // Проверка рекурсии
        File canonical = scriptFile.getCanonicalFile();
        if (!scriptStack.add(canonical)) {
            System.out.println("Обнаружена рекурсия! Выполнение скрипта " + scriptFile.getName() + " прервано.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(scriptFile);
             InputStreamReader fileReader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            StringBuilder lineBuilder = new StringBuilder();
            int ch;
            while ((ch = fileReader.read()) != -1) {
                if (ch == '\n') {
                    String line = lineBuilder.toString().trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        processScriptLine(line, fileReader);
                    }
                    lineBuilder = new StringBuilder();
                } else if (ch != '\r') {
                    lineBuilder.append((char) ch);
                }
            }
            // последняя строка
            String lastLine = lineBuilder.toString().trim();
            if (!lastLine.isEmpty() && !lastLine.startsWith("#")) {
                processScriptLine(lastLine, fileReader);
            }

            System.out.println("Скрипт " + scriptFile.getName() + " выполнен.");
            System.out.println();
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            System.out.println();
        } finally {
            scriptStack.remove(canonical);
        }
    }

    private void processScriptLine(String line, InputStreamReader fileReader) throws Exception {
        String[] parts = line.split("\\s+", 2);
        String cmdName = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";

        // Интерактивные команды не поддерживаются в скриптах
        if (cmdName.equals("add") || cmdName.equals("update") || cmdName.equals("add_if_min") ||
            cmdName.equals("remove_lower") || cmdName.equals("filter_less_than_status") ||
            cmdName.equals("filter_contains_name") || cmdName.equals("count_greater_than_position")) {
            System.out.println("Скрипт: команда " + cmdName + " требует интерактивного ввода и не поддерживается.");
            return;
        }

        commandReader.executeCommand(cmdName, arg, fileReader);
    }
}