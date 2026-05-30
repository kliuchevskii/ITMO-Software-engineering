package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;
import common.command.data.FilterStatusData;

import java.io.InputStreamReader;

public class FilterLessThanStatus extends Command {
    public FilterLessThanStatus(Client client) {
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
        System.out.println("Доступные статусы: Fired(1), Hired(2), Recommended_for_promotion(3), Regular(4)");
        System.out.print("Введите статус (id или название): ");
        String statusInput = readLine(reader);
        Response response = sendRequest(new Request(CommandType.FILTER_LESS_THAN_STATUS, new FilterStatusData(statusInput)));
        System.out.println(response.getMessage());
        System.out.println();
    }
}