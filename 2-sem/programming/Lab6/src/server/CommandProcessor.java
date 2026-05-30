package server;

import common.command.*;
import common.command.data.*;
import common.model.*;
import common.exception.IncorrectInputException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommandProcessor {
    private static final Logger logger = Logger.getLogger(CommandProcessor.class.getName());
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public CommandProcessor(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public Response process(Request request) {
        logger.info("Обработка команды: " + request.getCommandType());
        try {
            switch (request.getCommandType()) {
                case HELP:
                    return processHelp();
                case INFO:
                    return processInfo();
                case SHOW:
                    return processShow();
                case ADD:
                    return processAdd(request);
                case UPDATE:
                    return processUpdate(request);
                case CLEAR:
                    return processClear();
                case REMOVE_BY_ID:
                    return processRemoveById(request);
                case ADD_IF_MIN:
                    return processAddIfMin(request);
                case REMOVE_LOWER:
                    return processRemoveLower(request);
                case REMOVE_FIRST:
                    return processRemoveFirst();
                case FILTER_LESS_THAN_STATUS:
                    return processFilterLessThanStatus(request);
                case FILTER_CONTAINS_NAME:
                    return processFilterContainsName(request);
                case COUNT_GREATER_THAN_POSITION:
                    return processCountGreaterThanPosition(request);
                case UNDO:
                    return processUndo();
                case REDO:
                    return processRedo();
                case GET_WORKER_BY_ID:
                    return processGetWorkerById(request);
                default:
                    return new Response(false, "Неизвестная команда.", null);
            }
        } catch (Exception e) {
            logger.warning("Ошибка при обработке команды: " + e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    public Response processSave() {
        try {
            fileManager.save(collectionManager.getCollection());
            return new Response(true, "Коллекция сохранена в файл.", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка сохранения: " + e.getMessage(), null);
        }
    }

    private Response processHelp() {
        String help = """
                
                help - вывести справку.
                info - информация о коллекции.
                show - вывести все элементы.
                add - добавить работника.
                update {id} - обновить работника.
                clear - очистить коллекцию.
                remove_by_id - удалить по id.
                add_if_min - добавить если имя минимально.
                remove_lower - удалить элементы с id меньше заданного.
                remove_first - удалить первый элемент.
                filter_less_than_status - фильтр по статусу.
                filter_contains_name - фильтр по подстроке в имени.
                count_greater_than_position - количество с позицией больше заданной.
                undo - отменить последнее изменение.
                redo - повторить отменённое.
                exit - завершить клиент.
                """;
        return new Response(true, help, null);
    }

    private Response processInfo() {
        return new Response(true, collectionManager.getInfo(), null);
    }

    private Response processShow() {
    List<Worker> sortedByName = collectionManager.getSortedByName();
    if (sortedByName.isEmpty()) {
        return new Response(true, "Коллекция пуста.", sortedByName);
    }
    String result = sortedByName.stream()
            .map(Worker::toString)
            .collect(Collectors.joining("\n\n"));
    return new Response(true, result, sortedByName);
}

    // Вспомогательный метод валидации содержимого работника (без id и creationDate)
    private boolean validateWorkerContent(Worker worker) {
        if (worker.getName() == null || worker.getName().trim().isEmpty()) return false;
        if (worker.getCoordinates() == null || !worker.getCoordinates().validation()) return false;
        if (worker.getSalary() != null && worker.getSalary() <= 0) return false;
        if (worker.getPosition() == null) return false;
        if (worker.getStatus() == null) return false;
        // person может быть null
        return true;
    }

    private void copyWorkerFields(Worker target, Worker source) {
        target.setName(source.getName());
        target.setCoordinates(source.getCoordinates());
        if (source.getSalary() != null) {
            target.setSalary(String.valueOf(source.getSalary()));
        } else {
            target.setSalary("Null");
        }
        target.setPosition(source.getPosition() == null ? null : source.getPosition().toString());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().toString());
        target.setPerson(source.getPerson());
    }

    private Response processAdd(Request request) {
        collectionManager.saveState();
        Object data = request.getData();
        if (!(data instanceof AddData)) {
            return new Response(false, "Неверный формат данных для add.", null);
        }
        Worker received = ((AddData) data).getWorker();

        // Валидация полей (без id)
        if (!validateWorkerContent(received)) {
            return new Response(false, "Объект не прошёл валидацию полей.", null);
        }

        // Создаём нового работника с новым id
        Worker newWorker = new Worker();
        copyWorkerFields(newWorker, received);

        collectionManager.addToCollection(newWorker);
        return new Response(true, "Работник добавлен.\n" + newWorker, null);
    }

    private Response processUpdate(Request request) {
        collectionManager.saveState();
        Object data = request.getData();
        if (!(data instanceof UpdateData)) {
            return new Response(false, "Неверный формат данных для update.", null);
        }
        UpdateData upd = (UpdateData) data;
        long id = upd.getId();
        Worker newData = upd.getWorker();

        Optional<Worker> existingOpt = collectionManager.getWorkerById(id);
        if (existingOpt.isEmpty()) {
            return new Response(false, "Работник с id " + id + " не найден.", null);
        }
        Worker existing = existingOpt.get();

        // Обновляем поля существующего, не трогая id и creationDate
        copyWorkerFields(existing, newData);  // можно использовать тот же метод копирования

        if (!existing.validation()) {
            return new Response(false, "Обновлённый объект не прошёл валидацию.", null);
        }
        return new Response(true, "Работник с id " + id + " обновлён.\n" + existing, null);
    }

    private Response processClear() {
        collectionManager.saveState();
        collectionManager.clearCollection();
        return new Response(true, "Коллекция очищена.", null);
    }

    private Response processRemoveById(Request request) {
        collectionManager.saveState();
        Object data = request.getData();
        if (!(data instanceof RemoveByIdData)) {
            return new Response(false, "Неверный формат данных для remove_by_id.", null);
        }
        long id = ((RemoveByIdData) data).getId();
        Optional<Worker> opt = collectionManager.getWorkerById(id);
        if (opt.isEmpty()) {
            return new Response(false, "Работник с id " + id + " не найден.", null);
        }
        collectionManager.removeFromCollection(opt.get());
        return new Response(true, "Работник с id " + id + " удалён.", null);
    }

    private Response processAddIfMin(Request request) {
        collectionManager.saveState();
        Object data = request.getData();
        if (!(data instanceof AddData)) {
            return new Response(false, "Неверный формат данных для add_if_min.", null);
        }
        Worker received = ((AddData) data).getWorker();

        if (!validateWorkerContent(received)) {
            return new Response(false, "Объект не прошёл валидацию полей.", null);
        }

        Worker newWorker = new Worker();
        copyWorkerFields(newWorker, received);

        boolean added = collectionManager.addIfMin(newWorker);
        if (added) {
            return new Response(true, "Работник добавлен (имя минимально).\n" + newWorker, null);
        } else {
            return new Response(false, "Работник не добавлен: его имя не является наименьшим.", null);
        }
    }

    private Response processRemoveLower(Request request) {
        collectionManager.saveState();
        Object data = request.getData();
        if (!(data instanceof RemoveLowerByIdData)) {
            return new Response(false, "Неверный формат данных для remove_lower.", null);
        }
        long id = ((RemoveLowerByIdData) data).getId();
        boolean removed = collectionManager.removeIfIdLower(id);
        if (removed) {
            return new Response(true, "Удалены все работники с id < " + id, null);
        } else {
            return new Response(false, "Нет работников с id < " + id, null);
        }
    }

    private Response processRemoveFirst() {
        collectionManager.saveState();
        Optional<Worker> first = collectionManager.getLowestWorker();
        if (first.isEmpty()) {
            return new Response(false, "Коллекция пуста, нечего удалять.", null);
        }
        collectionManager.removeFromCollection(first.get());
        return new Response(true, "Удалён первый работник:\n" + first.get(), null);
    }

    private Response processFilterLessThanStatus(Request request) {
        Object data = request.getData();
        if (!(data instanceof FilterStatusData)) {
            return new Response(false, "Неверный формат данных для filter_less_than_status.", null);
        }
        String input = ((FilterStatusData) data).getStatusInput();
        try {
            Status threshold = Status.getStatus(input);
            List<Worker> filtered = collectionManager.filterLessThanStatus(threshold);
            if (filtered.isEmpty()) {
                return new Response(true, "Работников со статусом меньше " + threshold.name() + " не найдено.", null);
            }
            String result = filtered.stream().map(Worker::toString).collect(Collectors.joining("\n\n"));
            return new Response(true, result, filtered);
        } catch (IncorrectInputException e) {
            return new Response(false, e.getMessage(), null);
        }
    }

    private Response processFilterContainsName(Request request) {
        Object data = request.getData();
        if (!(data instanceof FilterContainsNameData)) {
            return new Response(false, "Неверный формат данных для filter_contains_name.", null);
        }
        String substring = ((FilterContainsNameData) data).getSubstring();
        List<Worker> filtered = collectionManager.filterContainsName(substring);
        if (filtered.isEmpty()) {
            return new Response(true, "Работников, чьё имя содержит \"" + substring + "\", не найдено.", null);
        }
        String result = filtered.stream().map(Worker::toString).collect(Collectors.joining("\n\n"));
        return new Response(true, result, filtered);
    }

    private Response processCountGreaterThanPosition(Request request) {
        Object data = request.getData();
        if (!(data instanceof CountPositionData)) {
            return new Response(false, "Неверный формат данных для count_greater_than_position.", null);
        }
        String input = ((CountPositionData) data).getPositionInput();
        try {
            Position threshold = Position.getPosition(input);
            long count = collectionManager.countGreaterThanPosition(threshold);
            return new Response(true, "Количество работников с позицией больше " + threshold.name() + ": " + count, null);
        } catch (IncorrectInputException e) {
            return new Response(false, e.getMessage(), null);
        }
    }

    private Response processUndo() {
        try {
            collectionManager.undo();
            return new Response(true, "Последнее изменение отменено.", null);
        } catch (IllegalStateException e) {
            return new Response(false, e.getMessage(), null);
        }
    }

    private Response processRedo() {
        try {
            collectionManager.redo();
            return new Response(true, "Команда повторена.", null);
        } catch (IllegalStateException e) {
            return new Response(false, e.getMessage(), null);
        }
    }

    private Response processGetWorkerById(Request request) {
        Object data = request.getData();
        if (!(data instanceof GetWorkerByIdData)) {
            return new Response(false, "Неверный формат данных для get_worker_by_id.", null);
        }
        long id = ((GetWorkerByIdData) data).getId();
        Optional<Worker> opt = collectionManager.getWorkerById(id);
        if (opt.isEmpty()) {
            return new Response(false, "Работник с id " + id + " не найден.", null);
        }
        return new Response(true, opt.get().toString(), opt.get());
    }
}