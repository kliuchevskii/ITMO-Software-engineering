package common.command;

public enum CommandType {
    HELP,
    INFO,
    SHOW,
    ADD,
    UPDATE,
    CLEAR,
    REMOVE_BY_ID,
    ADD_IF_MIN,
    REMOVE_LOWER,
    REMOVE_FIRST,
    FILTER_LESS_THAN_STATUS,
    FILTER_CONTAINS_NAME,
    COUNT_GREATER_THAN_POSITION,
    UNDO,
    REDO,
    GET_WORKER_BY_ID,
    EXIT  // только для клиента, но для единообразия
}