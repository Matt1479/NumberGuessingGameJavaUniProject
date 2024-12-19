package states.entity;

import java.util.Hashtable;

import states.BaseState;
import states.Util;

public class EntityBaseState extends BaseState {
    protected Entity entity;

    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get("entity");

        Util.log("EntityTestState.enter()");
        Util.log("Entity: " + this.entity);
    }

    public void update() {
        Util.log("EntityTestState.update()");
    }

    public void exit() {
        Util.log("EntityTestState.exit()");
    }
}
