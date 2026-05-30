package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;

import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import common.model.Worker;

public class Show extends Command {
    public Show(Client client) {
        super(client);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        // Проверяем, передан ли ключ сортировки по id (регистронезависимо)
        boolean sortById = false;
        if (input != null && !input.trim().isEmpty()) {
            String arg = input.trim().toLowerCase();
            sortById = arg.equals("-id") || arg.equals("-i");
        }

        System.out.println();

        Response response = sendRequest(new Request(CommandType.SHOW, null));
        if (!response.isSuccess()) {
            System.out.println(response.getMessage());
            return;
        }

        Object data = response.getData();
        if (data == null) {
            System.out.println("Коллекция пуста.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<Worker> workers = (List<Worker>) data;

        if (workers.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }

        // Если запрошена сортировка по id, пересортируем, иначе оставляем (сервер уже отсортировал по имени)
        if (sortById) {
            workers = workers.stream()
                    .sorted(Comparator.comparingLong(Worker::getId))
                    .collect(Collectors.toList());
        }

        String result = workers.stream()
                .map(Worker::toString)
                .collect(Collectors.joining("\n\n"));
        System.out.println(result);
        System.out.println();
    }
}