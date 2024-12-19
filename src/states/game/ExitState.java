package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateNames;
import states.Util;

public class ExitState extends BaseState {
    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");

        Util.log("\n" + this.stateName.toString().toLowerCase() + "State.enter()");
        Util.log("Exiting...");

        // Close scanner when done
        this.in.close();
        // Success
        System.exit(0);
    }
}
