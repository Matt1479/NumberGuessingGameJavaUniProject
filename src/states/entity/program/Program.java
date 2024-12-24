package states.entity.program;

import java.util.Hashtable;

import states.BaseState;
import states.StateNames;
import states.entity.Entity;

public class Program extends Entity {
    public Program(Hashtable<Object, Object> initParams) {
        // Call Entity's constructor
        super(initParams);
    }

    // Overwritten (overridden) methods
    @Override public void addState(StateNames stateName, BaseState state) {
        this.stateMachine.add(stateName, state);
    }
    @Override public void changeState(StateNames stateName, Hashtable<Object, Object> enterParams) {
        this.stateMachine.change(stateName, enterParams);
    }
    @Override public void update() {
        this.stateMachine.update();
    }
    @Override public void render() {
        this.stateMachine.render();
    }
}
