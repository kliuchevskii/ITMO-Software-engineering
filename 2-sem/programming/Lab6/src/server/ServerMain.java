package server;

import java.util.logging.*;

public class ServerMain {
    public static void main(String[] args) {
        // Настройка логирования
        configureLogging();
        
        String filePath = System.getenv("WORKERS");
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Переменная окружения WORKERS не установлена.");
            System.exit(1);
        }
        
        int port = 8080; // можно передать аргументом
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт, используется 8080");
            }
        }
        
        try {
            FileManager fileManager = new FileManager(filePath);
            CollectionManager collectionManager = new CollectionManager();
            CommandProcessor commandProcessor = new CommandProcessor(collectionManager, fileManager);
            
            // Загрузка коллекции из файла
            var workers = fileManager.load();
            workers.forEach(collectionManager::addToCollection);
            System.out.println("Загружено " + workers.size() + " элементов.");
            
            // Добавляем хук для сохранения при завершении
            Server server = new Server(port, collectionManager, commandProcessor);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nЗавершение работы сервера...");
                server.saveAndStop();
            }));
            
            server.start();
            
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка инициализации: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(handler);
    }
}