package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateMachine;
import states.StateNames;
import states.entity.EntityDataKeys;
import states.entity.player.Player;
import states.entity.player.PlayerStateFactory;
import states.entity.program.Program;
import states.entity.program.ProgramStateFactory;
import utility.Constants;
import utility.Util;

public class GamePlayState extends BaseState {
    Player p;
    Program program;

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get(DataKeys.gStateMachine);
        this.in = (Scanner) enterParams.get(DataKeys.in);
        this.stateName = (StateNames) enterParams.get(DataKeys.stateName);
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.loadParams(enterParams);

        this.p = new Player(null);
        // For every state...
        for (StateNames stateName : StateNames.values()) {
            // Use StateFactory to instantiate `PlayerState`s
            if (stateName.toString().startsWith("Player")) {
                this.p.addState(stateName, PlayerStateFactory.createState(stateName));
            }
        }

        this.program = new Program(null);
        // For every state...
        for (StateNames stateName : StateNames.values()) {
            // Use StateFactory to instantiate `ProgramState`s
            if (stateName.toString().startsWith("Program")) {
                this.program.addState(stateName, ProgramStateFactory.createState(stateName));
            }
        }
    }

    @Override public void exit() {}

    @Override public void update() {
        this.displayOptions();

        int choice = Util.getInt(this.in, '\n' + "Your choice: ");

        // Make sure the user's choice is valid (condition could be moved to Util.getInt())
        if (choice < 0 || choice > 5) {
            Util.log("Invalid choice. Please try again.");
            return;
        }
        
        switch (choice) {
            case 0:
                // Load player data (if it exists)
                this.p.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                }});

                // Change to PlayerGuessingState
                this.p.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                    put(EntityDataKeys.start, Constants.START);
                    put(EntityDataKeys.range, Constants.RANGE);
                    put(EntityDataKeys.seed, Constants.SEED);
                    // Player data
                    put(EntityDataKeys.chances, Constants.CHANCES);
                }});
                break;

            case 1:
                // Load program data (if it exists)
                this.program.changeState(StateNames.ProgramLoad, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                }});

                // Change to ProgramGuessingState
                this.program.changeState(StateNames.ProgramGuessing, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                    put(EntityDataKeys.start, Constants.START);
                    put(EntityDataKeys.range, Constants.RANGE);
                    put(EntityDataKeys.seed, Constants.SEED);
                    // Program data
                    put(EntityDataKeys.chances, Constants.CHANCES);
                }});
                break;
        
            case 2:
                Util.log("Not yet implemented.");
                break;

            case 3:
                Util.log("Not yet implemented.");
                break;

            case 4:
                Util.log("Not yet implemented.");
                break;

            case 5:
                this.gStateMachine.change(StateNames.GameExit, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put("program", program);
                    put(DataKeys.in, in);
                }});
                break;
        }

        // Update all of the entities
        this.p.update();
        this.program.update();
    }

    public void displayOptions() {
        Util.log("\nOptions:\n");
        int i = 0;
        Util.log(i + ". " + "Player is guessing");              i++;
        Util.log(i + ". " + "Reverse (Program is guessing)");   i++;
        Util.log(i + ". " + "Mixed");                           i++;
        Util.log(i + ". " + "Multi-Player");                    i++;
        Util.log(i + ". " + "Tournament");                      i++;
        Util.log(i + ". " + "Exit");                            i++;
    }
}
