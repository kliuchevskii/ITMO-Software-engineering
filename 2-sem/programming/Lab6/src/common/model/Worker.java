package common.model;

import common.Interface.Validation;
import common.exception.IncorrectInputException;
import common.util.NumbParser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Worker implements Comparable<Worker>, Validation {
    private static long currentId = 1;
    private long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Integer salary;
    private Position position;
    private Status status;
    private Person person;

    // Конструктор для новых работников (генерирует id)
    public Worker() {
        this.id = currentId++;
        this.creationDate = LocalDateTime.now();
    }

    // Конструктор копирования (не генерирует новый id)
    private Worker(Worker original) {
        this.id = original.id;
        this.name = original.name;
        this.coordinates = original.coordinates != null ? original.coordinates.copy() : null;
        this.creationDate = original.creationDate;
        this.salary = original.salary;
        this.position = original.position;
        this.status = original.status;
        this.person = original.person != null ? original.person.copy() : null;
        // Не трогаем static currentId
    }

    // Геттеры
    public long getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public Integer getSalary() { return salary; }
    public Position getPosition() { return position; }
    public Status getStatus() { return status; }
    public Person getPerson() { return person; }

    // Сеттеры с валидацией
    public void setName(String name) throws IncorrectInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new IncorrectInputException("не пустая строка");
        }
        if (!name.matches("^[a-zA-Z0-9\\s\\-\\'\\.]+$")) {
            throw new IncorrectInputException("имя должно содержать только латинские буквы, цифры, пробелы, дефисы, апострофы или точки");
        }
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setSalary(String salary) throws IncorrectInputException {
        if (salary == null || salary.trim().isEmpty()) {
            this.salary = null;
            return;
        }
        if (salary.equalsIgnoreCase("Null") || salary.equalsIgnoreCase("Nl")) {
            this.salary = null;
        } else {
            try {
                this.salary = NumbParser.parseInt(salary);
                if (this.salary <= 0) {
                    throw new NumberFormatException();
                }
            } catch (ArithmeticException | NumberFormatException e) {
                throw new IncorrectInputException("пустая строка (null), Null/Nl или число больше 0");
            }
        }
    }

    public void setPosition(String position) throws IncorrectInputException {
        this.position = Position.getPosition(position);
    }

    public void setStatus(String status) throws IncorrectInputException {
        this.status = Status.getStatus(status);
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public static long getCurrentId() { return currentId; }
    public static void updateCurrentId(long maxim) {
        if (maxim >= currentId) {
            currentId = maxim + 1;
        }
    }

    // Метод для сброса счётчика (опционально, для тестов)
    public static void resetCurrentId() {
        currentId = 1;
    }

    public Worker copy() {
        return new Worker(this); // используем конструктор копирования
    }

    @Override
    public int compareTo(Worker o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean validation() {
        if (id <= 0) return false;
        if (name == null || name.trim().isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (salary != null && salary <= 0) return false;
        if (position == null) return false;
        if (status == null) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return id == worker.id && creationDate.equals(worker.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Работник №").append(id)
          .append(" - ").append(name)
          .append(".\nКоординаты: ").append(coordinates)
          .append(".\nВремя создания: ").append(creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
          .append(".\nЗарплата: ").append(salary)
          .append(".\nПозиция: ").append(position)
          .append(".\nСтатус: ").append(status);
        if (person != null) {
            sb.append(".\n").append(person);
        }
        return sb.toString();
    }
}