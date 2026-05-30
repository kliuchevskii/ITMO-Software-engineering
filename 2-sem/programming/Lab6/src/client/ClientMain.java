package client;

public class ClientMain {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт, используется 8080");
            }
        }

        Client client = new Client(host, port);
        // 5 попыток подключения с интервалом 2 секунды
        if (!client.tryConnectWithRetry(5, 2000)) {
            System.exit(1);
        }

        ClientCommandReader reader = new ClientCommandReader(client);
        reader.start();
    }
}