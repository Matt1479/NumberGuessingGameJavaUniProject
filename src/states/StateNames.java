package states;

import utility.Util;

public enum StateNames {
    // Game
    GameStart,
    GamePlay,
    GameExit,
    // Entity
    EntityBase,
    // Player
    PlayerBase,
    PlayerIdle,
    PlayerGuessing,
    PlayerLoad,
    PlayerSave,
    // Program
    ProgramBase,
    ProgramIdle,
    ProgramGuessing,
    ProgramLoad,
    ProgramSave;

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