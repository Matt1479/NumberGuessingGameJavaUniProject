package states.entity.player;

import java.util.Hashtable;

import states.BaseState;
import states.StateNames;
import states.entity.Entity;

public class Player extends Entity {
    public Player(Hashtable<Object, Object> initParams) {
        // Call Entity's constructor
        super(initParams);
    }

    // Overwritten methods
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
