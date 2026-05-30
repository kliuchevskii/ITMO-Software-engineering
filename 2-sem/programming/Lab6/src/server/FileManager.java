package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import common.model.Worker;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {
    private final File file;
    private final Gson gson;

    public FileManager(String filePath) throws IllegalArgumentException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Переменная окружения WORKERS не установлена.");
        }
        this.file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("Файл не существует или является директорией: " + filePath);
        }
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new JsonTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public List<Worker> load() {
        try (Scanner scanner = new Scanner(file)) {
            StringBuilder json = new StringBuilder();
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
            var type = new TypeToken<ArrayList<Worker>>(){}.getType();
            List<Worker> workers = gson.fromJson(json.toString(), type);
            if (workers == null) return new ArrayList<>();
            // Обновляем статический счётчик ID
            workers.stream()
                    .mapToLong(Worker::getId)
                    .max()
                    .ifPresent(Worker::updateCurrentId);
            // Удаляем невалидные записи
            boolean removed = workers.removeIf(w -> !w.validation());
            if (removed) {
                System.out.println("В файле обнаружены невалидные объекты, они будут удалены при сохранении.");
            }
            return workers;
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void save(Collection<Worker> collection) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String json = gson.toJson(collection);
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения в файл: " + e.getMessage());
            // Попытка сохранить в резервный файл
            File backup = new File("backup_" + file.getName());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(backup))) {
                bw.write(gson.toJson(collection));
                System.out.println("Сохранено в резервный файл: " + backup.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Критическая ошибка: не удалось сохранить даже в резервный файл.");
            }
        }
    }

    private static class JsonTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}