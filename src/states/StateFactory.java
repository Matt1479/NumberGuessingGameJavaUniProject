package states;

import states.game.ExitState;
import states.game.PlayState;
import states.game.StartState;

public class StateFactory {
    public static BaseState createState(StateNames stateName) {
        switch (stateName) {
            case START:
                return new StartState();
            case PLAY:
                return new PlayState();
            case EXIT:
                return new ExitState();
            default:
                return new BaseState();
        }
    }
}
