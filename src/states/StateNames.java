package states;

public enum StateNames {
    // Game
    Start,
    Play,
    Exit,
    // Entity
    EntityBase,
    // Player
    PlayerBase;

    public static void displayOptions(StateNames doNotDisplay) {
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