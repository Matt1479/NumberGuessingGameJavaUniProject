import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateFactory;
import states.StateMachine;
import states.StateNames;
import utility.Settings;

public class App {
    static StateMachine gStateMachine;
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        load();

        while (true) {
            update();
            // draw();
        }
    }

    // Called once at the beginning
    public static void load() {
        Hashtable<StateNames, BaseState> states = new Hashtable<>();
        // For every state, add a key=value pair, where the key is
        // the stateName and the value is an instance a state
        for (StateNames stateName : StateNames.values()) {
            // Use StateFactory to instantiate `State`s
            states.put(stateName, StateFactory.createState(stateName));
        }

        gStateMachine = new StateMachine(states);
        gStateMachine.change(StateNames.GameStart, new Hashtable<Object, Object>() {{
            put(DataKeys.in, in);
            put(DataKeys.settings, new Settings());
        }});
    }

    // Called every frame (iteration of the loop)
    public static void update() {
        gStateMachine.update();
    }

    // Called every frame after update()
    // public static void draw() {
    //     gStateMachine.render();
    // }
}
