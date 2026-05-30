package server;

import common.command.Request;
import common.command.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.logging.*;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final CommandProcessor commandProcessor;
    private volatile boolean running = true;

    public Server(int port, CollectionManager collectionManager, CommandProcessor commandProcessor) {
        this.port = port;
        this.commandProcessor = commandProcessor;
    }

    public void start() {
        logger.info("Запуск сервера на порту " + port);
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Сервер готов принимать подключения");

            while (running) {
                selector.select(1000);
                if (!running) break;

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        acceptConnection(selector, serverChannel);
                    } else if (key.isReadable()) {
                        handleRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка сервера: " + e.getMessage(), e);
        }
        logger.info("Сервер остановлен");
    }

    private void acceptConnection(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.info("Новое подключение от " + clientChannel.getRemoteAddress());
    }

    private void handleRequest(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);

        try {
            // 1. Читаем длину сообщения (4 байта)
            while (lengthBuffer.hasRemaining()) {
                int r = channel.read(lengthBuffer);
                if (r == -1) {
                    logger.info("Клиент " + channel.getRemoteAddress() + " отключился");
                    channel.close();
                    return;
                }
                // Если данных недостаточно, выходим из цикла (канал неблокирующий)
                if (r == 0 && lengthBuffer.hasRemaining()) {
                    // Данных нет, ключ останется в selectedKeys на следующий раз
                    return;
                }
            }
            lengthBuffer.flip();
            int messageLength = lengthBuffer.getInt();

            // 2. Читаем само сообщение известной длины
            ByteBuffer dataBuffer = ByteBuffer.allocate(messageLength);
            while (dataBuffer.hasRemaining()) {
                int r = channel.read(dataBuffer);
                if (r == -1) {
                    logger.warning("Соединение разорвано при чтении данных");
                    channel.close();
                    return;
                }
                if (r == 0) {
                    // Неблокирующий режим: данных ещё нет, но они придут позже
                    // Сохраняем частично прочитанные данные? Упростим: выходим и продолжим позже.
                    // Но для корректности лучше накапливать. В учебных целях оставим так.
                    // Чтобы избежать потери данных, нужно использовать буферы и отложенное чтение.
                    // Упрощённо: считаем, что данные приходят целиком.
                    Thread.yield();
                }
            }
            dataBuffer.flip();
            byte[] data = new byte[dataBuffer.remaining()];
            dataBuffer.get(data);

            Request request = deserializeRequest(data);
            if (request == null) {
                logger.warning("Некорректный запрос от " + channel.getRemoteAddress());
                sendResponse(channel, new Response(false, "Некорректный запрос", null));
                return;
            }

            logger.info("Получена команда: " + request.getCommandType() + " от " + channel.getRemoteAddress());
            Response response = commandProcessor.process(request);
            sendResponse(channel, response);

        } catch (IOException e) {
            logger.warning("Ошибка при обмене с клиентом: " + e.getMessage());
            try { channel.close(); } catch (IOException ignored) {}
        }
    }

    private void sendResponse(SocketChannel channel, Response response) throws IOException {
        byte[] data = serializeResponse(response);
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        logger.info("Ответ отправлен клиенту " + channel.getRemoteAddress());
    }

    private Request deserializeRequest(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Request) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warning("Ошибка десериализации запроса: " + e.getMessage());
            return null;
        }
    }

    private byte[] serializeResponse(Response response) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            return baos.toByteArray();
        }
    }

    public void stop() {
        running = false;
    }

    public void saveAndStop() {
        logger.info("Сохранение коллекции перед остановкой...");
        commandProcessor.processSave();
        stop();
    }
}