package states.entity.player;

import states.StateNames;
import states.entity.EntityBaseState;

public class PlayerStateFactory {
    public static EntityBaseState createState(StateNames stateName) {
        switch (stateName) {
            case PlayerIdle:
                return new PlayerIdleState();
            case PlayerGuessing:
                return new PlayerGuessingState();
            case PlayerLoad:
                return new PlayerLoadState();
            case PlayerSave:
                return new PlayerSaveState();
            default:
                return new PlayerBaseState();
        }
    }
}
