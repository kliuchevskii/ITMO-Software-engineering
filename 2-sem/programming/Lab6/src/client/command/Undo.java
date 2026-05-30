package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;

import java.io.InputStreamReader;

public class Undo extends Command {
    public Undo(Client client) {
        super(client);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        Response response = sendRequest(new Request(CommandType.UNDO, null));
        System.out.println(response.getMessage());
        System.out.println();
    }
}