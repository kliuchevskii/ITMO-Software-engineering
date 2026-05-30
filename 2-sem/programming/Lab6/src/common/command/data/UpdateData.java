package common.command.data;

import common.model.Worker;
import java.io.Serializable;

public class UpdateData implements Serializable {
    private final long id;
    private final Worker worker;

    public UpdateData(long id, Worker worker) {
        this.id = id;
        this.worker = worker;
    }

    public long getId() {
        return id;
    }

    public Worker getWorker() {
        return worker;
    }
}