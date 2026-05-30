package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;
import common.command.data.FilterContainsNameData;

import java.io.InputStreamReader;

public class FilterContainsName extends Command {
    public FilterContainsName(Client client) {
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
        System.out.print("Введите подстроку для поиска в имени: ");
        String substring = readLine(reader);
        Response response = sendRequest(new Request(CommandType.FILTER_CONTAINS_NAME, new FilterContainsNameData(substring)));
        System.out.println(response.getMessage());
        System.out.println();
    }
}