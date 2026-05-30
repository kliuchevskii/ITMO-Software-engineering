package common.command.data;

import common.model.Worker;
import java.io.Serializable;

public class AddData implements Serializable {
    private final Worker worker;

    public AddData(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }
}