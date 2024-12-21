package states.entity;

import java.util.Hashtable;

import states.BaseState;
import states.StateMachine;
import states.StateNames;

// Base class for any Entity (i.e. Player)
public class Entity {
    protected Hashtable<Object, Object> initParams;
    protected StateMachine stateMachine;
    public Hashtable<Object, Object> data = new Hashtable<>();

    // Init/Constructor
    @SuppressWarnings("unchecked")
    public Entity(Hashtable<Object, Object> initParams) {
        this.initParams = initParams;

        if (initParams != null) {
            // If states are passed in
            if (initParams.containsKey("states")) {
                // Initialize StateMachine with those states
                this.stateMachine = new StateMachine(
                    (Hashtable<StateNames, BaseState>) initParams.get("states"));
            } else {
                this.stateMachine = new StateMachine(new Hashtable<>());
            }
        } else {
            this.stateMachine = new StateMachine(new Hashtable<>());
        }
    }

    // Methods
    public void addState(StateNames stateName, BaseState state) {
        this.stateMachine.add(stateName, state);
    }
    public void changeState(StateNames stateName, Hashtable<Object, Object> enterParams) {
        this.stateMachine.change(stateName, enterParams);
    }
    public void update() {
        this.stateMachine.update();
    }
    public void render() {
        this.stateMachine.render();
    }
}
