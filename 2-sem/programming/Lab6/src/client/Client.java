package client;

import common.command.Request;
import common.command.Response;

import java.io.*;
import java.net.*;

public class Client {
    private final String host;
    private final int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean connected = false;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        connected = true;
        System.out.println("Подключено к серверу " + host + ":" + port);
    }

    public boolean tryConnectWithRetry(int maxAttempts, long retryDelayMs) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                connect();
                return true;
            } catch (IOException e) {
                System.out.println("Не удалось подключиться к серверу (попытка " + attempt + "/" + maxAttempts + "): " + e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }
        System.err.println("Не удалось подключиться к серверу после " + maxAttempts + " попыток.");
        return false;
    }

    public Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        if (!connected) throw new IOException("Нет соединения с сервером");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
        }
        byte[] data = baos.toByteArray();
        out.writeInt(data.length);
        out.write(data);
        out.flush();

        int length = in.readInt();
        byte[] responseData = new byte[length];
        in.readFully(responseData);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Response) ois.readObject();
        }
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия соединения: " + e.getMessage());
        }
        connected = false;
    }

    public boolean isConnected() { return connected; }
}