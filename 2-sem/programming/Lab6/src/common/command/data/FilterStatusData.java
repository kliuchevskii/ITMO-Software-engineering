package common.command.data;

import java.io.Serializable;

public class FilterStatusData implements Serializable {
    private final String statusInput;

    public FilterStatusData(String statusInput) {
        this.statusInput = statusInput;
    }

    public String getStatusInput() {
        return statusInput;
    }
}