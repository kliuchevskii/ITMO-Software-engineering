package common.command;

import java.io.Serializable;

public class Request implements Serializable {
    private final CommandType commandType;
    private final Object data;

    public Request(CommandType commandType, Object data) {
        this.commandType = commandType;
        this.data = data;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Object getData() {
        return data;
    }
}