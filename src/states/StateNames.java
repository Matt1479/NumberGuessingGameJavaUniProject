package states;

public enum StateNames {
    START,
    PLAY,
    EXIT;

    public static void displayOptions(Object doNotDisplay) {
        int i = 0;
        Util.log("Options:");
        for (StateNames stateName : StateNames.values()) {
            if (!stateName.equals(doNotDisplay)) {
                Util.log(i + ". " + stateName);
            }
            i++;
        }
    }
}