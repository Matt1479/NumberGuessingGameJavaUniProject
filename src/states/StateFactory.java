package states;

import states.game.GameExitState;
import states.game.GamePlayState;
import states.game.GameStartState;

public class StateFactory {
    public static BaseState createState(StateNames stateName) {
        switch (stateName) {
            case GameStart:
                return new GameStartState();
            case GamePlay:
                return new GamePlayState();
            case GameExit:
                return new GameExitState();
            default:
                return new BaseState();
        }
    }
}
