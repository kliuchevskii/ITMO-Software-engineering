package common.command.data;

import java.io.Serializable;

public class GetWorkerByIdData implements Serializable {
    private final long id;

    public GetWorkerByIdData(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}