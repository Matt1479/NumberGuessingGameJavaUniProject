package states.entity.program;

import java.util.Hashtable;

import states.DataKeys;
import states.entity.Entity;
import states.entity.EntityBaseState;

public class ProgramBaseState extends EntityBaseState {
    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get(DataKeys.entity);
    }

    @Override public void update() {}

    @Override public void exit() {}
}
