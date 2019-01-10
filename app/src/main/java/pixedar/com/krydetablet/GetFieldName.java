package pixedar.com.krydetablet;

public enum GetFieldName {
    OUTSIDE_TEMP(0),
    INSIDE_TEMP(1),
    OUTSIDE_HUM(2),
    INSIDE_HUM(3),
    PRESSURE(4),
    AVERAGE_WIND(5),
    WIND(6),
    RAIN(7);

    private int index;

    GetFieldName(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
