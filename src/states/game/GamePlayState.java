package states.game;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import states.BaseState;
import states.DataKeys;
import states.StateMachine;
import states.StateNames;
import states.entity.EntityDataKeys;
import states.entity.player.Player;
import states.entity.player.PlayerIdleState;
import states.entity.player.PlayerLoadState;
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

    boolean changeDifficulty = false;

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

                this.p.data.put("tournament", false);
                this.p.data.put("multiPlayer", false);

                // Update the Player's stateMachine
                this.p.update();

                // Save data: change Player's state to PlayerSave
                this.p.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put(DataKeys.in, in);
                }});

                if (this.p.data.get(EntityDataKeys.hasWon).equals(true)) {
                    String input = Util.getString(this.in, "Do you want to change difficulty level (y/n): ");
                    if (Util.listContains(input, Arrays.asList("yes", "y"))) {
                        this.changeDifficulty = true;
                    }
                }

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

                this.p.data.put("tournament", false);
                this.p.data.put("multiPlayer", false);

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
                boolean playerTurn = r.nextInt(2) == 0;

                // Reset tries to 0
                this.p.data.put(EntityDataKeys.tries, 0);
                this.program.data.put(EntityDataKeys.tries, 0);

                this.p.data.put("tournament", false);
                this.p.data.put("multiPlayer", false);

                this.program.data.put("tournament", false);
                this.program.data.put("multiPlayer", false);
                
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

                    if (this.p.data.get(EntityDataKeys.hasWon).equals(true)) {
                        String input = Util.getString(this.in, "Do you want to change difficulty level (y/n): ");
                        if (Util.listContains(input, Arrays.asList("yes", "y"))) {
                            this.changeDifficulty = true;
                        }
                    }
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
                // Options
                Util.log("0. Display player data");
                Util.log("1. Begin multiplayer - normal mode");
                Util.log("2. Begin multiplayer - tournament mode");

                do {
                    // Prompt the user for choice
                    choice = Util.getInt(this.in, '\n' + "Your choice: ");
                } while (choice < 0 || choice > 2);

                if (choice == 0) {
                    Player currentPlayer = new Player(null);
                    currentPlayer.addState(StateNames.PlayerIdle, new PlayerIdleState());
                    currentPlayer.addState(StateNames.PlayerLoad, new PlayerLoadState());

                    // Load player data (if it exists)
                    currentPlayer.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
                        put(DataKeys.entity, currentPlayer);
                        put(DataKeys.in, in);
                        put(DataKeys.settings, settings);
                    }});

                    if (currentPlayer.data.getOrDefault(EntityDataKeys.newPlayer, true).equals(true)) {
                        Util.log("Player '" + currentPlayer.data.get(EntityDataKeys.name) + "' does not exist");
                    } else {
                        for (Map.Entry<Object, Object> entry : currentPlayer.data.entrySet()) {
                            Util.log(entry.getKey() + ":" + entry.getValue());
                        }
                    }

                } else if (choice == 1 || choice == 2) {
                    int numPlayers = 0;

                    // Normal mode
                    if (choice == 1) {
                        do {
                            // Prompt for numPlayersMult
                            numPlayers = Util.getInt(in, "Number of players (> 1): ");
                        } while (numPlayers <= 1);
                    } else if (choice == 2) {
                        do {
                            // Prompt for numPlayersMult
                            numPlayers = Util.getInt(in, "Number of players (> 1, even): ");
                        } while (numPlayers <= 1 || numPlayers % 2 == 1);
                    }
                    this.settings.setNumPlayersMult(numPlayers);
    
                    // Hashtable of players where key = player's name, value = instance of a Player
                    Hashtable<String, Player> players = new Hashtable<String, Player>();
    
                    /* Init: instantiate players, add them to hashtable */
                    // For every Player...
                    for (int i = 0, n = this.settings.getNumPlayersMult(); i < n; i++) {
                        // Add a key, value pair
                        String playerName = Util.getString(in, "Player " + i + "'s" + " name: ");
                        players.put(playerName, new Player(null));
    
                        Player currentPlayer = players.get(playerName);
    
                        // For every state...
                        for (StateNames stateName : StateNames.values()) {
                            // Use StateFactory to instantiate each Player's `PlayerState`s
                            if (stateName.toString().startsWith("Player")) {
                                currentPlayer.addState(stateName, PlayerStateFactory.createState(stateName));
                            }
                        }
    
                        // Add currentPlayer's name to his data
                        currentPlayer.data.put(EntityDataKeys.name, playerName);
    
                        // Load player data (if it exists) and set some data
                        currentPlayer.changeState(StateNames.PlayerLoad, new Hashtable<>() {{
                            put(DataKeys.entity, currentPlayer);
                            put(DataKeys.in, in);
                            put(DataKeys.settings, settings);
                        }});
    
                        // Set necessary data for this mode
                        currentPlayer.data.put(EntityDataKeys.hasWon, false);
                        currentPlayer.data.put(EntityDataKeys.tries, 0);
                        // Multiplayer params
                        currentPlayer.data.put("multiPlayer", true);

                        // If normal mode
                        if (choice == 1) {
                            currentPlayer.data.put("tournament", false);
                        }

                        // If tournament
                        if (choice == 2) {
                            // n-th player
                            currentPlayer.data.put("n", i);
                            currentPlayer.data.put("tournament", true);
                            currentPlayer.data.put("wins", 0);
                        }
                    }
    
                    // If multiplayer normal mode
                    if (choice == 1) {
                        // Init
                        int rand = settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart());
                        int swap;
                        // Make sure swap is different from rand
                        do {
                            swap = settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart());
                        } while (swap == rand);
                        boolean swapped = false;
                        isWinner = false;
                        Player winner = null;
        
                        while (!isWinner) {
                            // For each key, value pair in players hash table...
                            for (Map.Entry<String, Player> entry : players.entrySet()) {
                                int guessTarget;

                                Player currentPlayer = entry.getValue();

                                // If champion (can swap guessTarget number once), and not swapped yet
                                if (currentPlayer.data.getOrDefault("champion", false).equals("true")
                                    && !swapped
                                    // 1 in 2 chance to occur
                                    && r.nextInt(2) == 0) {

                                    Util.log("You are the champion! You have the right to swap the guess target once.");
                                    String answer = Util.getString(this.in, "Do you want to swap the guess target (y/n): ");

                                    if (Util.listContains(answer, Arrays.asList("yes", "y"))) {
                                        guessTarget = swap;
                                        swapped = true;
                                    } else {
                                        guessTarget = rand;
                                    }
                                } else {
                                    if (swapped) {
                                        guessTarget = swap;
                                    } else {
                                        guessTarget = rand;
                                    }
                                }

                                // Change currentPlayer's state to PlayerGuessingState
                                currentPlayer.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                                    put(DataKeys.entity, currentPlayer);
                                    put(DataKeys.in, in);
                                    put(EntityDataKeys.start, settings.getStart());
                                    put(EntityDataKeys.range, settings.getRange());
                                    put(EntityDataKeys.seed, settings.getSeed());
                                    // Player data
                                    put(EntityDataKeys.chances, 1);
                                    put(EntityDataKeys.tries, currentPlayer.data.get(EntityDataKeys.tries));
                                    // Guess params
                                    put(EntityDataKeys.guessParams, new Hashtable<Object, Object>() {{
                                        put(EntityDataKeys.playerGuessTarget, guessTarget);
                                    }});
                                    // Multiplayer params
                                    put("multiPlayer", true);
                                }});
    
                                // If leader - has 2 moves
                                if (currentPlayer.data.getOrDefault("leader", false).equals("true")) {
                                    // Update the currentPlayer's stateMachine - update the previous state change
                                    currentPlayer.update();
    
                                    // If leader didn't win yet, he can do another move
                                    if (!currentPlayer.data.get(EntityDataKeys.hasWon).equals(true)) {
                                        // Change currentPlayer's state to PlayerGuessingState
                                        currentPlayer.changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                                            put(DataKeys.entity, currentPlayer);
                                            put(DataKeys.in, in);
                                            put(EntityDataKeys.start, settings.getStart());
                                            put(EntityDataKeys.range, settings.getRange());
                                            put(EntityDataKeys.seed, settings.getSeed());
                                            // Player data
                                            put(EntityDataKeys.chances, 1);
                                            put(EntityDataKeys.tries, currentPlayer.data.get(EntityDataKeys.tries));
                                            // Guess params
                                            put(EntityDataKeys.guessParams, new Hashtable<Object, Object>() {{
                                                put(EntityDataKeys.playerGuessTarget, guessTarget);
                                            }});
                                            // Multiplayer params
                                            put("multiPlayer", true);
                                        }});
                                    }
                                }
                                
                                // Update the currentPlayer's stateMachine
                                currentPlayer.update();
        
                                // Check if currentPlayer has won
                                isWinner = currentPlayer.data.get(EntityDataKeys.hasWon).equals(true);
        
                                if (isWinner) {
                                    winner = currentPlayer;
                                    break;
                                }
                            }
                        }
        
                        // Declare winner and set necessary data
                        Util.log("Player " + winner.data.get(EntityDataKeys.name) + " has won!");
        
                        // For each key, value pair in players hash table...
                        for (Map.Entry<String, Player> entry : players.entrySet()) {
                            Player currentPlayer = entry.getValue();
        
                            if (currentPlayer.data.get(EntityDataKeys.hasWon).equals(true)) {
                                // Increment the winning player's number of wins
                                currentPlayer.data.put("numWins",
                                    (Integer.parseInt(currentPlayer.data.get("numWins").toString()) + 1));
                                
                                // Give the winning player a new key:value pair: {"leader":true}
                                currentPlayer.data.put("leader", true);
                            } else {
                                // Increment the losing player's number of losses
                                currentPlayer.data.put("numLosses",
                                    (Integer.parseInt(currentPlayer.data.get("numLosses").toString()) + 1));
                                
                                // Give the losing player a new key:value pair: {"leader":false}
                                currentPlayer.data.put("leader", false);
                            }
                        }
                    } else {
                        Util.log("\n+=======================Tournament=======================+\n");
                        Util.log("Tournament types, for example:");
                        // best of 1 (1 game per round, needs 1 win to win)
                        Util.log("  Best of 1 (1 game  per round);");
                        // best of 3 (3 games per round, needs 2/3 wins to win)
                        Util.log("  Best of 3 (3 games per round);");
                        // best of 5 (5 games per round, needs 3/5 wins to win)
                        Util.log("  Best of 5 (5 games per round);");
                        // best of n (n games per round, needs >50% wins to win)
                        Util.log("  Best of n (n games per round);");
                        Util.log("You will need to get the majority of wins to win a tournament bracket.");

                        int bestOf;
                        do {
                            // Prompt the user for bestOf
                            bestOf = Util.getInt(this.in, '\n' + "Input n (games per round, > 0): ");
                        } while (bestOf < 0);

                        boolean shufflePlayers = false;
                        String answer = Util.getString(this.in, "Do you want to shuffle players after each round (y/n): ");
                        if (Util.listContains(answer, Arrays.asList("yes", "y"))) {
                            shufflePlayers = true;
                        }

                        boolean hasChampion = false;
                        Player champion = null;

                        while (!hasChampion) {
                            // Tournament bracket
                            // n: n-th player
                            for (int n = 0, size = players.size(); n < size; n = n + 2) {
                                // Init
                                isWinner = false;
                                Player winner = null;

                                Player[] pair = {null, null};

                                // Get (n)-th and (n + 1)-th player
                                for (Map.Entry<String, Player> entry : players.entrySet()) {
                                    if (entry.getValue().data.get("n").equals(n)) {
                                        pair[0] = entry.getValue();
                                    }
                                    if (entry.getValue().data.get("n").equals(n + 1)) {
                                        pair[1] = entry.getValue();
                                    }
                                }

                                // bestOf n (n games per round, needs majority of wins to win)
                                for (int numGames = 0; numGames < bestOf; numGames++)
                                {
                                    int guessTarget = settings.getStart() + r.nextInt(settings.getRange() + 1 - settings.getStart());

                                    // Competition between a pair of players
                                    while (!isWinner) {
                                        if (pair[0] == null || pair[1] == null) {
                                            break;
                                        }

                                        // Util.log(pair[0].data.get("name") + " and " + pair[1].data.get("name") + " are competing!");
    
                                        for (int j = 0; j < 2; j++) {
                                            // Weird Java stuff
                                            final int index = j;
    
                                            // Change pair[index]'s state to PlayerGuessingState
                                            pair[index].changeState(StateNames.PlayerGuessing, new Hashtable<>() {{
                                                put(DataKeys.entity, pair[index]);
                                                put(DataKeys.in, in);
                                                put(EntityDataKeys.start, settings.getStart());
                                                put(EntityDataKeys.range, settings.getRange());
                                                put(EntityDataKeys.seed, settings.getSeed());
                                                // Player data
                                                put(EntityDataKeys.chances, 1);
                                                put(EntityDataKeys.tries, pair[index].data.get(EntityDataKeys.tries));
                                                // Guess params
                                                put(EntityDataKeys.guessParams, new Hashtable<Object, Object>() {{
                                                    put(EntityDataKeys.playerGuessTarget, guessTarget);
                                                }});
                                            }});
                                            
                                            // Update the pair[index]'s stateMachine
                                            pair[index].update();
                    
                                            // Check if pair[index] has won
                                            isWinner = pair[index].data.get(EntityDataKeys.hasWon).equals(true);
                    
                                            if (isWinner) {
                                                winner = pair[index];
                                                int wins = Integer.parseInt(winner.data.get("wins").toString());
                                                // Increment the number of wins in this tournament
                                                winner.data.put("wins", ++wins);

                                                // Winner has the majority of wins, he is the winner of this bracket
                                                // Only remove the other player if this condition is true
                                                if (((float) wins / (float) bestOf) > 0.5) {
                                                    // Get losingPlayer
                                                    Player losingPlayer = pair[index == 0 ? 1 : 0];

                                                    // Util.log(losingPlayer.data.get("name") + " is out!");

                                                    /* Save losing player's data */
                                                    // Increment the losing player's number of losses
                                                    losingPlayer.data.put("numLosses",
                                                        (Integer.parseInt(losingPlayer.data.get("numLosses").toString()) + 1));
                                                    // This player is not the champion
                                                    losingPlayer.data.put("champion", false);
                                                    // Remove n
                                                    losingPlayer.data.remove("n");
                                                    // Remove wins since it is not relevant
                                                    losingPlayer.data.remove("wins");
                                                    // Save data: change losingPlayer's state to PlayerSave
                                                    losingPlayer.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                                                        put(DataKeys.entity, losingPlayer);
                                                        put(DataKeys.in, in);
                                                    }});
                                                    // Remove the Player from the hash table of `Player`s
                                                    players.remove(losingPlayer.data.get(EntityDataKeys.name));
                                                }
    
                                                // If only 1 player left, he is the champion
                                                if (players.size() == 1) {
                                                    hasChampion = true;
                                                    champion = winner;
                                                    champion.data.put("champion", true);
                                                }

                                                break;
                                            }
                                        }
                                    }
                                    
                                    // If winner has the majority of wins, skip the remaining rounds
                                    int wins = winner != null ? Integer.parseInt(winner.data.get("wins").toString()) : 0;
                                    if (((float) wins / (float) bestOf) > 0.5) {
                                        break;
                                    }

                                    // Reset
                                    isWinner = false;
                                }
                            }

                            /* Used for sorted or random position assignment */
                            // create a TreeMap
                            TreeMap<String, Player> treeMap = new TreeMap<>(players); 
                            // create a keyset
                            Set<String> keys = treeMap.keySet(); 
                            Iterator<String> itr = keys.iterator();

                            int counter = 0;

                            int htSize = players.size();
                            Integer[] spots = new Integer[htSize];
                            for (int i = 0; i < htSize; i++) {
                                spots[i] = 0;
                            }

                            // Iterate over remaining players and re-assign their positions
                            // traverse the TreeMap using iterator
                            while (itr.hasNext()) {
                                String name = itr.next();

                                // Reset wins to 0
                                treeMap.get(name).data.put("wins", 0);

                                if (shufflePlayers) {
                                    int randIndex = r.nextInt(htSize);
                                    while (spots[randIndex] == 1) {
                                        randIndex = r.nextInt(htSize);
                                    }

                                    // Mark spots[randIndex] as taken
                                    spots[randIndex] = 1;

                                    // Assign n (in random order)
                                    treeMap.get(name).data.put("n", randIndex);
                                } else {
                                    // Assign n (in sorted order)
                                    treeMap.get(name).data.put("n", counter);
                                }

                                counter++;
                            }

                            // for (Map.Entry<String, Player> entry : players.entrySet()) {
                            //     Util.log(entry.getValue().data.get("name") + "'s n: " + entry.getValue().data.get("n"));
                            // }
                        }

                        Util.log("The champion of the tournament: " + champion.data.get(EntityDataKeys.name));

                        Util.log("\n+========================================================+\n");
                    }

                    // Set some data and save it
                    // For each key, value pair in players hash table...
                    for (Map.Entry<String, Player> entry : players.entrySet()) {
                        Player currentPlayer = entry.getValue();

                        if (currentPlayer.data.get(EntityDataKeys.hasWon).equals(true)) {
                            // leastTries
                            int currentPlayerTries = Integer.parseInt(currentPlayer.data.get(EntityDataKeys.tries).toString());
                            int currentPlayerLeastTries = Integer.parseInt(currentPlayer.data.get(EntityDataKeys.leastTries).toString());
                            if (currentPlayerTries < currentPlayerLeastTries) {
                                currentPlayer.data.put(EntityDataKeys.leastTries, currentPlayerTries);
                            }
                        }
                        // Save data: change currentPlayer's state to PlayerSave
                        currentPlayer.changeState(StateNames.PlayerSave, new Hashtable<>() {{
                            put(DataKeys.entity, currentPlayer);
                            put(DataKeys.in, in);
                        }});
                        
                        // Change currentPlayer's state to PlayerIdle
                        currentPlayer.changeState(StateNames.PlayerIdle, new Hashtable<>() {{
                            put(DataKeys.entity, currentPlayer);
                            put(DataKeys.in, in);
                        }});
                    }

                    // No need for an if-check here, there is always a winner
                    String input = Util.getString(this.in, "Do you want to change difficulty level (y/n): ");
                    if (Util.listContains(input, Arrays.asList("yes", "y"))) {
                        this.changeDifficulty = true;
                    }
                }

                break;

            case 4:
                this.gStateMachine.change(StateNames.GameExit, new Hashtable<>() {{
                    put(DataKeys.entity, p);
                    put("program", program);
                    put(DataKeys.in, in);
                    put(DataKeys.settings, settings);
                }});
                break;
        }

        if (this.changeDifficulty) {
            // Change to SettingsState
            this.gStateMachine.change(StateNames.GameSettings, new Hashtable<>() {{
                put(DataKeys.in, in);
                put(DataKeys.settings, settings);
            }});

            // Reset to default value
            this.changeDifficulty = false;
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
        Util.log(i + ". " + "Exit");                            i++;
    }
}
