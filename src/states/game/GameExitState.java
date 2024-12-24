package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateNames;
import states.entity.Entity;

public class GameExitState extends BaseState {
    private Entity entity;

    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);
        this.stateName = (StateNames) enterParams.get(DataKeys.stateName);

        this.entity.changeState(StateNames.PlayerSave, new Hashtable<>() {{
            put(DataKeys.entity, entity);
            put(DataKeys.in, in);
        }});

        // Close scanner when done
        this.in.close();
        // Success
        System.exit(0);
    }
}
