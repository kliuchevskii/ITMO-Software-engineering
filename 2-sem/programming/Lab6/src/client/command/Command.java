package client.command;

import client.Client;
import common.command.Request;
import common.command.Response;

import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Command {
    protected final Client client;

    public Command(Client client) {
        this.client = client;
    }

    public abstract void execute(String input, InputStreamReader reader) throws Exception;

    protected Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        return client.sendRequest(request);
    }
}