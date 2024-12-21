package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateMachine;
import states.StateNames;

public class StartState extends BaseState {

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get("gStateMachine");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.loadParams(enterParams);

        // Change to PlayState, passing (Scanner) in, for now
        this.gStateMachine.change(StateNames.Play, new Hashtable<>() {{
            put("in", in);
        }});

        // StateNames.displayOptions(this.stateName);
    }

    @Override public void exit() {}

    @Override public void update() {
        // this.e.update();
        // this.p.update();

        /*
        int choice = Util.getInt(this.in, "Your choice: ");

        // Make sure the user's choice is valid (condition could be moved to Util.getInt())
        if (choice < 0 || choice >= StateNames.values().length || choice == ((StateNames) this.stateName).ordinal()) {
            Util.log("Invalid choice. Please try again.");
            return;
        }

        StateNames selectedState = StateNames.values()[choice];

        switch (selectedState) {
            case Play:
                this.gStateMachine.change(StateNames.Play, new Hashtable<>() {{
                    put("in", in);
                }});
                break;
            case Exit:
                this.gStateMachine.change(StateNames.Exit, new Hashtable<>() {{
                    put("in", in);
                }});
                break;
            default:
                break;
        }
        */
    }
}
