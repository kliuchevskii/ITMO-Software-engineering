package client.command;

import client.Client;
import java.io.InputStreamReader;
import java.util.List;

public class History extends Command {
    private final List<String> commandHistory;

    public History(Client client, List<String> commandHistory) {
        super(client);
        this.commandHistory = commandHistory;
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        System.out.println("\nПоследние 5 команд:");
        for (String cmd : commandHistory) {
            System.out.println("  " + cmd);
        }
        System.out.println();
    }
}