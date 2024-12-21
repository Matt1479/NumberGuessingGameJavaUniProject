package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateNames;
import states.Util;
import states.entity.Entity;

public class GameExitState extends BaseState {
    private Entity entity;

    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get("entity");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");

        this.entity.changeState(StateNames.PlayerSave, new Hashtable<>() {{
            put("entity", entity);
            put("in", in);
        }});

        // Close scanner when done
        this.in.close();
        // Success
        System.exit(0);
    }
}
