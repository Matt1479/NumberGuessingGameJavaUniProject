package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateMachine;
import states.StateNames;
import states.Util;

public class StartState extends BaseState {
    // Constructor (init)
    // public StartState() { };

    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get("gStateMachine");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = enterParams.get("stateName");

        Util.log("\n" + this.stateName.toString().toLowerCase() + "State.enter()");

        StateNames.displayOptions(this.stateName);
    }

    public void exit() {
        Util.log(this.stateName.toString().toLowerCase() + "State.exit()");
    }

    public void update() {
        Util.log(this.stateName.toString().toLowerCase() + "State.update()");
        int choice = Util.getInt(this.in, "Your choice: ");

        // Make sure the user's choice is valid
        if (choice < 0 || choice >= StateNames.values().length || choice == ((StateNames) this.stateName).ordinal()) {
            Util.log("Invalid choice. Please try again.");
            return;
        }

        StateNames selectedState = StateNames.values()[choice];

        switch (selectedState) {
            case PLAY:
                this.gStateMachine.change(StateNames.PLAY, new Hashtable<>() {{
                    put("in", in);
                }});
                break;
            case EXIT:
                this.gStateMachine.change(StateNames.EXIT, new Hashtable<>() {{
                    put("in", in);
                }});
                break;
            default:
                break;
        }
    }
}
