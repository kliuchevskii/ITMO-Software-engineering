package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;

import java.io.InputStreamReader;

public class Info extends Command {
    public Info(Client client) {
        super(client);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        Response response = sendRequest(new Request(CommandType.INFO, null));
        System.out.println(response.getMessage());
    }
}