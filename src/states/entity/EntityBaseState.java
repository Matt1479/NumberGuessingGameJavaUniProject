package states.entity;

import java.util.Hashtable;

import states.BaseState;
import states.Util;

public class EntityBaseState extends BaseState {
    public void enter(Hashtable<Object, Object> enterParams) {
        Util.log("EntityTestState.enter()");
    }

    public void update() {
        Util.log("EntityTestState.update()");
    }

    public void exit() {
        Util.log("EntityTestState.exit()");
    }
}
