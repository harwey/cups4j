package org.cups4j;

public enum PrinterStateEnum {
    IDLE(3), PRINTING(4), STOPPED(5);

    private Integer value;

    PrinterStateEnum(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public Integer getValue() { return value; }

    public static PrinterStateEnum fromInteger(Integer value) {
        if (value != null) {
            for (PrinterStateEnum printerState : PrinterStateEnum.values()) {
                if (value == printerState.value) {
                    return printerState;
                }
            }
        }
        return null;
    }
    public static PrinterStateEnum fromString(String value) {
        if (value != null) {
            for (PrinterStateEnum printerState: PrinterStateEnum.values()) {
                if (value.equalsIgnoreCase(printerState.value.toString())) {
                    return printerState;
                }
            }
        }
        return null;
    }
}