package states.entity;

import java.util.Hashtable;

import states.BaseState;

public class EntityBaseState extends BaseState {
    protected Entity entity;

    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get("entity");
    }

    public void update() {}

    public void exit() {}
}
