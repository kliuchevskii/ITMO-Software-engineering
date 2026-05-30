package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;
import common.command.data.CountPositionData;

import java.io.InputStreamReader;

public class CountGreaterThanPosition extends Command {
    public CountGreaterThanPosition(Client client) {
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
        System.out.println("Доступные позиции: HUMAN_RESOURCES(1), HEAD_OF_DEPARTMENT(2), LEAD_DEVELOPER(3), MANAGER_OF_CLEANING(4)");
        System.out.print("Введите позицию (id или название): ");
        String positionInput = readLine(reader);
        Response response = sendRequest(new Request(CommandType.COUNT_GREATER_THAN_POSITION, new CountPositionData(positionInput)));
        System.out.println(response.getMessage());
        System.out.println();
    }
}