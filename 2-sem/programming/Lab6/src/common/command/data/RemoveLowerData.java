package common.command.data;

import common.model.Worker;
import java.io.Serializable;

public class RemoveLowerData implements Serializable {
    private final Worker worker;

    public RemoveLowerData(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }
}