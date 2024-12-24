package states.entity.program;

import states.StateNames;
import states.entity.EntityBaseState;

public class ProgramStateFactory {
    public static EntityBaseState createState(StateNames stateName) {
        switch (stateName) {
            case ProgramIdle:
                return new ProgramIdleState();
            case ProgramGuessing:
                return new ProgramGuessingState();
            case ProgramLoad:
                return new ProgramLoadState();
            case ProgramSave:
                return new ProgramSaveState();
            default:
                return new ProgramBaseState();
        }
    }
}
