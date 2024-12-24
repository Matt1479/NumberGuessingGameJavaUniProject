package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateNames;
import states.entity.player.Player;
import states.entity.program.Program;
import utility.Util;

public class GameExitState extends BaseState {
    private Player p;
    private Program program;

    // Methods
    public void enter(Hashtable<Object, Object> enterParams) {
        this.p = (Player) enterParams.get(DataKeys.entity);
        this.program = (Program) enterParams.get("program");
        this.in = (Scanner) enterParams.get(DataKeys.in);
        this.stateName = (StateNames) enterParams.get(DataKeys.stateName);

        if (!this.p.data.isEmpty()) {
            this.p.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                put(DataKeys.entity, p);
                put(DataKeys.in, in);
            }});
        }

        if (!this.program.data.isEmpty()) {
            this.program.changeState(StateNames.ProgramSave, new Hashtable<>() {{
                put(DataKeys.entity, program);
                put(DataKeys.in, in);
            }});
        }

        // Close scanner when done
        this.in.close();
        // Success
        System.exit(0);
    }
}
