package states;

import states.game.ExitState;
import states.game.PlayState;
import states.game.StartState;

public class StateFactory {
    public static BaseState createState(StateNames stateName) {
        switch (stateName) {
            case Start:
                return new StartState();
            case Play:
                return new PlayState();
            case Exit:
                return new ExitState();
            default:
                return new BaseState();
        }
    }
}
