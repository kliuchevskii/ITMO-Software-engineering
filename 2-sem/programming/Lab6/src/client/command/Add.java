package client.command;

import client.Client;
import common.command.CommandType;
import common.command.Request;
import common.command.Response;
import common.command.data.AddData;
import common.exception.ExecuteException;
import common.exception.IncorrectInputException;
import common.model.*;

import java.io.IOException;
import java.io.InputStreamReader;

public class Add extends Command {
    private boolean isSystemReader = true;
    private Worker finalWorker;
    private Coordinates finalCoordinates;
    private Person finalPerson;
    private static final int YES = 1;
    private static final int NO = 0;

    public Add(Client client) {
        super(client);
    }

    private int needPerson(String input, InputStreamReader reader) throws ExecuteException, IOException {
        if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) return YES;
        else if (input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) return NO;
        else if (isSystemReader) {
            System.out.print("Введите yes/no: ");
            return needPerson(readLine(reader), reader);
        } else throw new ExecuteException(getClass().getSimpleName());
    }

    private String readLine(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1 && ch != '\n') {
            if (ch != '\r') sb.append((char) ch);
        }
        return sb.toString();
    }

    private void announce(String message, String conditions) {
        if (isSystemReader) {
            String suffix = (conditions != null && !conditions.isBlank()) ? " (" + conditions + ")" : "";
            System.out.print("\n" + message + suffix + ": ");
        }
    }

    private void writeWorkerName(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalWorker.setName(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeWorkerName(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeSalary(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalWorker.setSalary(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeSalary(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writePosition(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalWorker.setPosition(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writePosition(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeStatus(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalWorker.setStatus(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeStatus(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeCoordinateX(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalCoordinates.setX(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeCoordinateX(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeCoordinateY(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalCoordinates.setY(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeCoordinateY(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeBirthday(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalPerson.setBirthday(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeBirthday(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeEyeColor(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalPerson.setEyeColor(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeEyeColor(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeHairColor(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalPerson.setHairColor(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeHairColor(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void writeNationality(String input, InputStreamReader reader) throws ExecuteException, IOException {
        try {
            finalPerson.setNationality(input);
        } catch (IncorrectInputException e) {
            if (isSystemReader) {
                System.out.print(e.getMessage());
                writeNationality(readLine(reader), reader);
            } else throw new ExecuteException(getClass().getSimpleName());
        }
    }

    private void createPerson(InputStreamReader reader) throws ExecuteException, IOException {
        announce("Будет ли у работника персональные данные?", "yes/no");
        switch (needPerson(readLine(reader), reader)) {
            case YES:
                announce("Введите дату рождения человека", "dd-MM-yyyy HH:mm:ss или Null/Nl");
                writeBirthday(readLine(reader), reader);
                announce("Введите цвет глаз человека", Color.getColorsInfo());
                writeEyeColor(readLine(reader), reader);
                announce("Введите цвет волос человека", Color.getColorsInfo());
                writeHairColor(readLine(reader), reader);
                announce("Введите национальность человека", Country.getNationalityInfo());
                writeNationality(readLine(reader), reader);
                break;
            case NO:
                finalPerson = null;
        }
    }

    private void createCoordinates(InputStreamReader reader) throws ExecuteException, IOException {
        announce("Введите координату X", "целое > -258");
        writeCoordinateX(readLine(reader), reader);
        announce("Введите координату Y", "число с плавающей точкой не более 3 знаков после запятой");
        writeCoordinateY(readLine(reader), reader);
    }

    private void createWorker(InputStreamReader reader) throws ExecuteException, IOException {
        announce("Введите имя работника", "не пустая строка");
        writeWorkerName(readLine(reader), reader);
        announce("Введите зарплату работника", "Null/Nl или число больше 0");
        writeSalary(readLine(reader), reader);
        announce("Введите позицию работника", Position.getNationalityInfo());
        writePosition(readLine(reader), reader);
        announce("Введите статус работника", Status.getNationalityInfo());
        writeStatus(readLine(reader), reader);
        finalWorker.setCoordinates(finalCoordinates);
        finalWorker.setPerson(finalPerson);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        isSystemReader = true;
        finalWorker = new Worker();
        finalPerson = new Person();
        finalCoordinates = new Coordinates();

        createCoordinates(reader);
        createPerson(reader);
        createWorker(reader);
        System.out.println();

        Request request = new Request(CommandType.ADD, new AddData(finalWorker));
        Response response = sendRequest(request);
        System.out.println(response.getMessage());
        System.out.println();
    }
}