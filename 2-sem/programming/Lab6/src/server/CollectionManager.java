package server;

import common.model.Worker;
import common.model.Status;
import common.model.Position;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final Deque<Worker> collection = new ArrayDeque<>();
    private final LocalDateTime creationTime;
    private Worker betterWorker = null;

    // Стеки для undo/redo
    private final Deque<Deque<Worker>> undoStack = new ArrayDeque<>();
    private final Deque<Deque<Worker>> redoStack = new ArrayDeque<>();
    

    public CollectionManager() {
        this.creationTime = LocalDateTime.now();
    }

    public String getInfo() {
        return "\nТип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                "Время создания: " + creationTime + "\n" +
                "Тип элемента: Worker\n" +
                "Размер коллекции: " + collection.size() + "\n";
    }

    public Deque<Worker> getCollection() {
        return collection;
    }

    public Optional<Worker> getWorkerById(long id) {
        return collection.stream()
                .filter(w -> w.getId() == id)
                .findFirst();
    }

    public Optional<Worker> getMinWorkerByName() {
        return collection.stream()
                .min(Comparator.comparing(Worker::getName));
    }

    public Optional<Worker> getWorkerWithMinId() {
        return collection.stream()
                .min(Comparator.comparingLong(Worker::getId));
    }

    public Optional<Worker> getBetterWorker() {
        return Optional.ofNullable(betterWorker);
    }

    public Optional<Worker> getLowestWorker() {
        return Optional.ofNullable(collection.peek());
    }

    public void addToCollection(Worker worker) {
        collection.add(worker);
        System.out.println("В коллекцию добавлен новый работник: \n\n" + worker + "\n");
        if (betterWorker == null || worker.compareTo(betterWorker) > 0) {
            betterWorker = worker;
        }
    }

    public void removeFromCollection(Worker worker) {
        collection.remove(worker);
        System.out.println("Из коллекции удален сотрудник:\n" + worker + "\n");
        if (betterWorker != null && betterWorker.equals(worker)) {
            recalcBetterWorker();
        }
    }

    public void clearCollection() {
        collection.clear();
        betterWorker = null;
        System.out.println("Коллекция очищена.\n");
    }

    public List<Worker> getSortedByName() {
        return collection.stream()
                .sorted(Comparator.comparing(Worker::getName))
                .collect(Collectors.toList());
    }

    public List<Worker> filterContainsName(String substring) {
        return collection.stream()
                .filter(w -> w.getName() != null && w.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Worker> filterLessThanStatus(Status threshold) {
        return collection.stream()
                .filter(w -> w.getStatus() != null && w.getStatus().getId() < threshold.getId())
                .collect(Collectors.toList());
    }

    public long countGreaterThanPosition(Position threshold) {
        return collection.stream()
                .filter(w -> w.getPosition() != null && w.getPosition().getId() > threshold.getId())
                .count();
    }

    public boolean removeIfIdLower(long id) {
        boolean removed = collection.removeIf(w -> w.getId() < id);
        if (removed) recalcBetterWorker();
        return removed;
    }

    public boolean addIfMin(Worker worker) {
        Optional<Worker> minOpt = getMinWorkerByName();
        if (minOpt.isEmpty() || worker.compareTo(minOpt.get()) < 0) {
            addToCollection(worker);
            return true;
        }
        return false;
    }

    public void saveState() {
        Deque<Worker> copy = collection.stream()
                .map(Worker::copy)
                .collect(Collectors.toCollection(ArrayDeque::new));
        undoStack.push(copy);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            throw new IllegalStateException("Нечего отменять.");
        }
        Deque<Worker> current = collection.stream()
                .map(Worker::copy)
                .collect(Collectors.toCollection(ArrayDeque::new));
        redoStack.push(current);

        collection.clear();
        collection.addAll(undoStack.pop());
        recalcBetterWorker();
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            throw new IllegalStateException("Нечего повторять.");
        }
        Deque<Worker> current = collection.stream()
                .map(Worker::copy)
                .collect(Collectors.toCollection(ArrayDeque::new));
        undoStack.push(current);

        collection.clear();
        collection.addAll(redoStack.pop());
        recalcBetterWorker();
    }

    private void recalcBetterWorker() {
        betterWorker = collection.stream()
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

}