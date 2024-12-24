package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateMachine;
import states.StateNames;
import states.Util;
import states.entity.player.Player;

import states.entity.player.PlayerStateFactory;

public class GamePlayState extends BaseState {
    Player p;

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get("gStateMachine");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.loadParams(enterParams);

        // this.e = new Entity(null);
        // this.e.addState(StateNames.EntityBase, new EntityBaseState());
        // this.e.changeState(StateNames.EntityBase, new Hashtable<>() {{
        //     put("entity", e);
        // }});

        this.p = new Player(null);
        // For every state...
        for (StateNames stateName : StateNames.values()) {
            // Use StateFactory to instantiate `PlayerState`s
            if (stateName.toString().startsWith("Player")) {
                this.p.addState(stateName, PlayerStateFactory.createState(stateName));
            }
        }

        // Load player data (if it exists)
        this.p.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
            put("entity", p);
            put("in", in);
        }});

        // Change to PlayerGuessingState
        this.p.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
            put("entity", p);
            put("in", in);
            put("start", 0);
            put("range", 100);
            put("seed", -1);
            // Player data
            put("chances", 10);
        }});
    }

    @Override public void exit() {}

    @Override public void update() {
        this.p.update();

        // String choice = Util.getString(this.in, "Do you want to try again (y/n): ");
        // if (Util.listContains(choice, Arrays.asList("yes", "y"))) {
        //     this.p.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
        //         // Entity(Player), Scanner
        //         put("entity", p);
        //         put("in", in);
        //         // Random's params
        //         put("start", 0);
        //         put("range", 100);
        //         put("seed", -1);
        //         // Player data
        //         put("chances", 10);
        //     }});
        // } else if (Util.listContains(choice, Arrays.asList("no", "n"))) {
        //     // Change the game's state to GameExitState
        //     this.gStateMachine.change(StateNames.GameExit, new Hashtable<>() {{
        //         put("entity", p);
        //         put("in", in);
        //     }});
        // }

        this.gStateMachine.change(StateNames.GameExit, new Hashtable<>() {{
            put("entity", p);
            put("in", in);
        }});
    }
}
