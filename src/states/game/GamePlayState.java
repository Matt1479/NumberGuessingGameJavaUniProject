package states.game;

import java.util.Hashtable;
import java.util.Random;
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
import utility.Settings;
import utility.Util;

public class GamePlayState extends BaseState {
    private Settings settings;

    Player p;
    Program program;

    Random r;

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get(DataKeys.gStateMachine);
        this.in = (Scanner) enterParams.get(DataKeys.in);
        this.stateName = (StateNames) enterParams.get(DataKeys.stateName);

        this.settings = (Settings) enterParams.get(DataKeys.settings);
    }

    /* Suppressing the following warnings:
        * comparing identical expressions: once you change settings.getSeed()'s value, they are different
        * unused/dead code: it is used, it depends on settings.getSeed()'s value
     */
    @Override @SuppressWarnings("all") public void enter(Hashtable<Object, Object> enterParams) {
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

        this.r = settings.getSeed() == -1 ? new Random() : new Random(settings.getSeed());
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
                    put(DataKeys.settings, settings);
                }});

                // Change to PlayerGuessingState
                this.p.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                    put(EntityDataKeys.start, settings.getStart());
                    put(EntityDataKeys.range, settings.getRange());
                    put(EntityDataKeys.seed, settings.getSeed());
                    // Player data
                    put(EntityDataKeys.chances, settings.getChances());
                    // Settings
                    put(DataKeys.settings, settings);
                }});

                // Update the Player's stateMachine
                this.p.update();

                // Save data: change Player's state to PlayerSave
                this.p.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                }});

                break;

            case 1:
                // Load program data (if it exists)
                this.program.changeState(StateNames.ProgramLoad, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});

                // Change to ProgramGuessingState
                this.program.changeState(StateNames.ProgramGuessing, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                    put(EntityDataKeys.start, settings.getStart());
                    put(EntityDataKeys.range, settings.getRange());
                    put(EntityDataKeys.seed, settings.getSeed());
                    // Program data
                    put(EntityDataKeys.chances, settings.getChances());
                }});

                // Update the Program's stateMachine
                this.program.update();

                // Save data: change Program's state to ProgramSave
                this.program.changeState(StateNames.ProgramSave, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                }});

                break;
        
            case 2:
                // Load player data (if it exists)
                this.p.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
                // Load program data (if it exists)
                this.program.changeState(StateNames.ProgramLoad, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
                Util.log("", true);

                // Init
                int playerGuessTarget = settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart());
                int programGuessTarget = settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart());
                boolean isWinner = false;
                boolean playerTurn = r.nextInt(2) == 1;

                // Reset tries to 0
                this.p.data.put(EntityDataKeys.tries, 0);
                this.program.data.put(EntityDataKeys.tries, 0);
                
                while (!isWinner) {
                    if (playerTurn) {
                        // Change to PlayerGuessingState
                        this.p.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                            put(DataKeys.entity, p);
                            put(DataKeys.in, in);
                            put(EntityDataKeys.start, settings.getStart());
                            put(EntityDataKeys.range, settings.getRange());
                            put(EntityDataKeys.seed, settings.getSeed());
                            // Player data
                            put(EntityDataKeys.chances, 1);
                            put(EntityDataKeys.tries, p.data.get(EntityDataKeys.tries));
                            // Guess params
                            put(EntityDataKeys.guessParams, new Hashtable<Object, Object>() {{
                                put(EntityDataKeys.playerGuessTarget, playerGuessTarget);
                            }});
                        }});

                        // Update the Player's stateMachine
                        this.p.update();

                        // Check if Player has won
                        isWinner = this.p.data.get(EntityDataKeys.hasWon).equals(true);
                    } else {
                        // Change to ProgramGuessingState
                        this.program.changeState(StateNames.ProgramGuessing, new Hashtable<>() {{
                            put(DataKeys.entity, program);
                            put(DataKeys.in, in);
                            put(EntityDataKeys.start,
                                program.data.containsKey(EntityDataKeys.start)
                                ? program.data.get(EntityDataKeys.start)
                                : settings.getStart());
                            put(EntityDataKeys.range,
                                program.data.containsKey(EntityDataKeys.range)
                                ? program.data.get(EntityDataKeys.range)
                                : settings.getRange());
                            put(EntityDataKeys.seed, settings.getSeed());
                            // Program data
                            put(EntityDataKeys.chances, 1);
                            put(EntityDataKeys.tries, program.data.get(EntityDataKeys.tries));
                            // Guess params
                            put(EntityDataKeys.guessParams, new Hashtable<Object, Object>() {{
                                put(EntityDataKeys.programGuessTarget, programGuessTarget);
                                put(EntityDataKeys.programGuess,
                                    program.data.containsKey(EntityDataKeys.start)
                                    &&
                                    program.data.containsKey(EntityDataKeys.range)
                                    ? Integer.parseInt(program.data.get(EntityDataKeys.start).toString())
                                        + r.nextInt(
                                            Integer.parseInt(program.data.get(EntityDataKeys.range).toString())
                                            + 1 - Integer.parseInt(program.data.get(EntityDataKeys.start).toString()))
                                    : settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart()));
                            }});
                        }});

                        // Update the program's stateMachine
                        this.program.update();

                        // Check if Program has won
                        isWinner = this.program.data.get(EntityDataKeys.hasWon).equals(true);
                    }

                    // Switch turns
                    playerTurn = !playerTurn;
                }

                // Remove/reset key/value pairs
                this.p.data.remove(EntityDataKeys.guessParams);
                this.program.data.remove(EntityDataKeys.guessParams);
                this.program.data.remove(EntityDataKeys.start);
                this.program.data.remove(EntityDataKeys.range);

                // Declare winner and set necessary data
                if (this.p.data.get(EntityDataKeys.hasWon).equals(true)) {
                    Util.log("Player wins!");

                    // leastTries
                    int playerTries = Integer.parseInt(this.p.data.get(EntityDataKeys.tries).toString());
                    int playerLeastTries = Integer.parseInt(this.p.data.get(EntityDataKeys.leastTries).toString());
                    if (playerTries < playerLeastTries) {
                        this.p.data.put(EntityDataKeys.leastTries, playerTries);
                    }

                    this.p.data.put("numWins",
                        (Integer.parseInt(this.p.data.get("numWins").toString()) + 1));
                    this.program.data.put("numLosses",
                    (Integer.parseInt(this.program.data.get("numLosses").toString()) + 1));
                } else {
                    Util.log("Program wins!");

                    // leastTries
                    int programTries = Integer.parseInt(this.program.data.get(EntityDataKeys.tries).toString());
                    int programLeastTries = Integer.parseInt(this.program.data.get(EntityDataKeys.leastTries).toString());
                    if (programTries < programLeastTries) {
                        this.program.data.put(EntityDataKeys.leastTries, programTries);
                    }

                    this.program.data.put("numWins",
                    (Integer.parseInt(this.program.data.get("numWins").toString()) + 1));
                    this.p.data.put("numLosses",
                    (Integer.parseInt(this.p.data.get("numLosses").toString()) + 1));
                }

                // Save data: change Player's state to PlayerSave
                this.p.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                }});
                // Save data: change Program's state to ProgramSave
                this.program.changeState(StateNames.ProgramSave, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                }});

                // Change Player's state to PlayerIdle
                this.p.changeState(StateNames.PlayerIdle, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                }});
                // Change Program's state to ProgramIdle
                this.program.changeState(StateNames.ProgramIdle, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                }});
                
                break;

            case 3:
                // Load player data (if it exists)
                this.p.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
                // Load program data (if it exists)
                this.program.changeState(StateNames.ProgramLoad, new Hashtable<>() {{
                    put(DataKeys.entity, program);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
            
                Util.log('\n' + "player's number of wins: " + this.p.data.get("numWins"));
                Util.log("player's number of losses: " + this.p.data.get("numLosses") + '\n');
                Util.log("program's number of wins: " + this.program.data.get("numWins"));
                Util.log("program's number of losses: " + this.program.data.get("numLosses"));

                break;

            case 4:
                Util.log("Not yet implemented.");
                break;

            case 5:
                this.gStateMachine.change(StateNames.GameExit, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put("program", program);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
                break;
        }

        // Update all of the entities (when Idle, it does nothing)
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
