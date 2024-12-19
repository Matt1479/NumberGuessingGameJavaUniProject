package states.entity.player;

import java.util.Hashtable;

import states.Util;
import states.entity.Entity;
import states.entity.EntityBaseState;

public class PlayerBaseState extends EntityBaseState {
    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get("entity");

        Util.log("PlayerTestState.enter()");
        Util.log("Entity: " + this.entity);
    }

    public void update() {
        Util.log("PlayerTestState.update()");
    }

    public void exit() {
        Util.log("PlayerTestState.exit()");
    }
}
