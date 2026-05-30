package common.command.data;

import java.io.Serializable;

public class CountPositionData implements Serializable {
    private final String positionInput;

    public CountPositionData(String positionInput) {
        this.positionInput = positionInput;
    }

    public String getPositionInput() {
        return positionInput;
    }
}