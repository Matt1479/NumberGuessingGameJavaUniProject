package states.entity.player;

import java.util.Hashtable;

import states.Util;
import states.entity.EntityBaseState;

public class PlayerBaseState extends EntityBaseState {
    public void enter(Hashtable<Object, Object> enterParams) {
        Util.log("PlayerTestState.enter()");
    }

    public void update() {
        Util.log("PlayerTestState.update()");
    }

    public void exit() {
        Util.log("PlayerTestState.exit()");
    }
}
