package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateMachine;
import states.StateNames;
import states.Util;
import states.entity.Entity;
import states.entity.EntityBaseState;
import states.entity.player.Player;
import states.entity.player.PlayerBaseState;

public class StartState extends BaseState {
    // Constructor (init)
    // public StartState() { };

    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get("gStateMachine");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");

        Util.log("\n" + this.stateName.toString().toLowerCase() + "State.enter()");

        StateNames.displayOptions(this.stateName);

        Entity e = new Entity(null);
        e.addState(StateNames.EntityBase, new EntityBaseState());
        e.changeState(StateNames.EntityBase, new Hashtable<>() {{
            put("entity", e);
        }});

        Player p = new Player(null);
        p.addState(StateNames.PlayerBase, new PlayerBaseState());
        p.changeState(StateNames.PlayerBase, new Hashtable<>() {{
            put("entity", p);
        }});
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
    }
}
