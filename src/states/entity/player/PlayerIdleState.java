package states.entity.player;

import java.util.Hashtable;


import states.entity.Entity;
import states.entity.EntityBaseState;

public class PlayerIdleState extends EntityBaseState {
    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get("entity");
    }

    @Override public void update() {}

    @Override public void exit() {}
}
