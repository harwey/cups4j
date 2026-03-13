package org.cups4j;

public enum PrinterStateEnum {
    IDLE(3, "idle"),
    PRINTING(4, "printing"),
    STOPPED(5, "stopped");

    private final int value;
    private final String stateName;

    PrinterStateEnum(int value, String stateName) {
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
    
    /**
     * @param stateName name of the printer state
     * @return printer state enum for name or null
     */
    public static PrinterStateEnum fromStateName(String stateName) {
        if (stateName != null) {
            for (PrinterStateEnum printerState : PrinterStateEnum.values()) {
                if (stateName.equalsIgnoreCase(printerState.getStateName())) {
                    return printerState;
                }
            }
        }
        return null;
    }
}
