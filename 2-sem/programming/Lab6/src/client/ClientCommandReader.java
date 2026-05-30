package client;

import client.command.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ClientCommandReader {
    private final Client client;
    private final Map<String, Command> commands = new HashMap<>();
    private final LinkedList<String> commandHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 5;

    public ClientCommandReader(Client client) {
        this.client = client;
        initCommands();
    }

    private void initCommands() {
        commands.put("help", new Help(client));
        commands.put("info", new Info(client));
        commands.put("show", new Show(client));
        commands.put("add", new Add(client));
        commands.put("update", new Update(client));
        commands.put("clear", new Clear(client));
        commands.put("remove_by_id", new RemoveById(client));
        commands.put("add_if_min", new AddIfMin(client));
        commands.put("remove_lower", new RemoveLower(client));
        commands.put("remove_first", new RemoveFirst(client));
        commands.put("filter_less_than_status", new FilterLessThanStatus(client));
        commands.put("filter_contains_name", new FilterContainsName(client));
        commands.put("count_greater_than_position", new CountGreaterThanPosition(client));
        commands.put("undo", new Undo(client));
        commands.put("redo", new Redo(client));
        commands.put("history", new History(client, commandHistory));
        commands.put("execute_script", new ExecuteScript(client, this));
        commands.put("exit", new Exit(client));
    }

    public void executeCommand(String commandName, String argument, InputStreamReader reader) throws Exception {
        Command cmd = commands.get(commandName);
        if (cmd != null) {
            cmd.execute(argument, reader);
        } else {
            System.out.println("Неизвестная команда в скрипте: " + commandName);
        }
    }

    public void start() {
        InputStreamReader consoleReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        System.out.println("Клиент запущен. Введите help для справки.");
        while (true) {
            try {
                System.out.print("> ");
                String input = readLine(consoleReader);
                if (input == null) break;

                String trimmed = input.trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String argument = parts.length > 1 ? parts[1] : "";

                Command cmd = commands.get(commandName);
                if (cmd != null) {
                    cmd.execute(argument, consoleReader);
                    if (!commandName.equals("history")) {
                        addToHistory(input);
                    }
                } else {
                    System.out.println("Неизвестная команда. Введите help.");
                }
            } catch (IOException e) {
                System.err.println("Ошибка связи с сервером: " + e.getMessage());
                System.out.println("Попытка переподключения...");
                reconnect();
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка при получении ответа от сервера: " + e.getMessage());
                System.out.println("Попытка переподключения...");
                reconnect();
            } catch (Exception e) {
                System.err.println("Ошибка выполнения команды: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Попытка переподключения...");
                reconnect();
            }
        }
        client.close();
    }

    private void addToHistory(String command) {
        commandHistory.add(command);
        if (commandHistory.size() > HISTORY_SIZE) {
            commandHistory.removeFirst();
        }
    }

    private void reconnect() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(2000);
                client.connect();
                System.out.println("Переподключение успешно.");
                return;
            } catch (IOException | InterruptedException e) {
                System.out.println("Не удалось переподключиться, попытка " + (i+1) + "/5");
            }
        }
        System.err.println("Не удалось восстановить соединение. Клиент завершает работу.");
        System.exit(1);
    }

    private String readLine(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1 && ch != '\n') {
            if (ch != '\r') sb.append((char) ch);
        }
        return sb.length() == 0 && ch == -1 ? null : sb.toString();
    }
}