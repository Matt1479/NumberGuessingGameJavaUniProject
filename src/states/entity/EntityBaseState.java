package states.entity;

import java.util.Hashtable;

import states.BaseState;
import states.DataKeys;

public class EntityBaseState extends BaseState {
    protected Entity entity;

    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get(DataKeys.entity);
    }

    public void update() {}

    public void exit() {}
}
