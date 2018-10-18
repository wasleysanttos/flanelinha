package util;

/**
 * Created by WasleySantos on 26/01/2017.
 */

public class Contas {


    private String key;
    private String value;

    public Contas(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
    return  this.getValue();
    }
}
