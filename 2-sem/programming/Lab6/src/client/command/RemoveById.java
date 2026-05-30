package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;
import common.command.data.RemoveByIdData;

import java.io.InputStreamReader;

public class RemoveById extends Command {
    public RemoveById(Client client) {
        super(client);
    }

    private String readLine(InputStreamReader reader) throws java.io.IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1 && ch != '\n') {
            if (ch != '\r') sb.append((char) ch);
        }
        return sb.toString();
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        System.out.println("\nСписок работников в коллекции:");
        Response listResponse = sendRequest(new Request(CommandType.SHOW, null));
        System.out.println(listResponse.getMessage());
        System.out.println(); // пустая строка для разделения
        System.out.print("Введите id работника, которого хотите удалить: ");
        String idLine = readLine(reader);
            long id;
            try {
                id = Long.parseLong(idLine.trim());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный id.");
                return;
            }
            Response response = sendRequest(new Request(CommandType.REMOVE_BY_ID, new RemoveByIdData(id)));
            System.out.println(response.getMessage());
            System.out.println();
        }
}