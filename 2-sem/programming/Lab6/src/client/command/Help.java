package client.command;

import client.Client;
import java.io.InputStreamReader;

public class Help extends Command {
    private static final String COMMANDS = """
            
            help - вывести справку.

            info - вывести информацию о коллекции.

            show - вывести все элементы коллекции (сортировка по имени); (-id / -i) - сортировка по id.

            add - добавить нового работника.

            update - обновить работника по id, будет дан ознакомительный список.

            clear - очистить коллекцию.

            remove_by_id - удалить работника по id, будет дан ознакомительный список.

            add_if_min - добавить работника, если его имя минимально по алфавиту.

            remove_lower - удалить работников с id меньше заданного.

            remove_first - удалить работника с наименьшим id.

            execute_script <script-file_name> - запустить скрипт

            filter_less_than_status - вывести работников с меньшим статусом, чем заданный, будет дан ознакомительный список.

            filter_contains_name - вывести работника, чьё имя совпадает с введенной строкой.

            count_greater_than_position - количество работников с позицией, больше заданной, будет дан ознакомительный список.

            undo - отменить последнее изменение.

            redo - повторить отменённое.
            
            history - показать последние 5 команд.
            
            exit - завершить клиент.
            
            """;

    public Help(Client client) {
        super(client);
    }

    @Override
    public void execute(String input, InputStreamReader reader) throws Exception {
        System.out.println(COMMANDS);
    }
}