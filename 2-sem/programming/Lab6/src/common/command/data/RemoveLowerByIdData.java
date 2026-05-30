package common.command.data;

import java.io.Serializable;

public class RemoveLowerByIdData implements Serializable {
    private final long id;

    public RemoveLowerByIdData(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}