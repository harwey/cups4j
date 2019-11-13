package org.cups4j;

public enum PrinterStateEnum {
    IDLE(3, "idle"), PRINTING(4, "printing"), STOPPED(5, "stopped");

    private Integer value;
    private String stateName;

    PrinterStateEnum(Integer value, String stateName) {
        this.value = value;
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return this.stateName;
    }

    public Integer getValue() {
        return value;
    }
    public String getStateName() {
        return stateName;
    }

    public static PrinterStateEnum fromInteger(Integer value) {
        if (value != null) {
            for (PrinterStateEnum printerState : PrinterStateEnum.values()) {
                if (value == printerState.getValue()) {
                    return printerState;
                }
            }
        }
        return null;
    }

    public static PrinterStateEnum fromStringInteger(String value) {
        if (value != null) {
            for (PrinterStateEnum printerState : PrinterStateEnum.values()) {
                if (value.equalsIgnoreCase(printerState.getValue().toString())) {
                    return printerState;
                }
            }
        }
        return null;
    }
}