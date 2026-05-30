package common.command.data;

import java.io.Serializable;

public class RemoveByIdData implements Serializable {
    private final long id;

    public RemoveByIdData(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}