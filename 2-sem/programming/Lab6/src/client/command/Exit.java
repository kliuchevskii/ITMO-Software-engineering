package client.command;

import client.Client;
import java.io.InputStreamReader;

public class Exit extends Command {
    public Exit(Client client) {
        super(client);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        System.out.println("Завершение клиента.");
        System.exit(0);
    }
}