package common.command.data;

import java.io.Serializable;

public class FilterContainsNameData implements Serializable {
    private final String substring;

    public FilterContainsNameData(String substring) {
        this.substring = substring;
    }

    public String getSubstring() {
        return substring;
    }
}