package states.entity.player;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import states.DataKeys;
import states.StateNames;
import states.entity.EntityBaseState;
import states.entity.EntityDataKeys;
import utility.Constants;
import utility.Settings;
import utility.Util;

public class PlayerGuessingState extends EntityBaseState {
    private Scanner in;
    private Settings settings;

    private int playerChances;
    private int start;
    private int range;
    private long seed;

    // Keeps track of how many tries it took the user to guess the number, if they did
    private int playerTries;
    // Keeps track of the least tries it took a Player to win
    private int playerLeastTries;
    // Whether we want to greet the player and log their information...
    private boolean greetPlayer = true;

    // Whether it is the mixed gameplay
    boolean mixed;

    // Whether it is multiplayer
    boolean multiPlayer;

    // Whether it is tournament
    boolean tournament;

    // Methods
    public void unpack(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.entity = (Player) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);

        this.start = Integer.parseInt(enterParams.get(EntityDataKeys.start).toString());
        this.range = Integer.parseInt(enterParams.get(EntityDataKeys.range).toString());
        this.seed = Integer.parseInt(enterParams.get(EntityDataKeys.seed).toString());

        this.playerChances = Integer.parseInt(enterParams.getOrDefault(EntityDataKeys.chances, 1).toString());
        this.playerLeastTries = Integer.parseInt(this.entity.data.get(EntityDataKeys.leastTries).toString());

        this.mixed = enterParams.containsKey(EntityDataKeys.guessParams);

        this.playerTries = this.mixed
            ? Integer.parseInt(this.entity.data.get(EntityDataKeys.tries).toString())
            : 0;
        
        this.settings = (Settings) enterParams.get(DataKeys.settings);
        
        this.multiPlayer = (boolean) this.entity.data.getOrDefault(EntityDataKeys.multiPlayer, false);
        this.tournament = (boolean) this.entity.data.getOrDefault(EntityDataKeys.tournament, false);
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.unpack(enterParams);

        if (this.mixed) {
            Util.log("+--------------------------------------------------------+");
            if (this.multiPlayer) {
                if (tournament) {
                    if (this.entity.data.getOrDefault(EntityDataKeys.champion, false).equals(true)) {
                        Util.log("[Champion] ", false);
                    }
                } else {
                    if (this.entity.data.getOrDefault(EntityDataKeys.leader, false).equals("true")) {
                        Util.log("[Leader] ", false);
                    }
                }
                Util.log("Player: " + this.entity.data.get(EntityDataKeys.name) + "'s" + " turn!\n");
            } else {
                Util.log("Player's turn!\n");
            }
        } else {
            if (this.entity.data.get(EntityDataKeys.newPlayer).equals(false) && greetPlayer) {
                Util.log("\nHello, " + this.entity.data.get(EntityDataKeys.name) + "!");
    
                if (this.entity.data.containsKey(EntityDataKeys.hasWon)) {
                    if (this.entity.data.get(EntityDataKeys.hasWon).toString().equals("true")) {
                        Util.log("You have won the last time!");
                    } else {
                        Util.log("You haven't won the last time.");
                    }
                }
                Util.log("Your least amount of tries to win: " + this.playerLeastTries);
    
                String choice = Util.getString(this.in, "Do you want to start over (y/n): ");
                if (Util.listContains(choice, Arrays.asList("yes", "y"))) {
                    this.playerLeastTries = settings.getChances();
                    this.entity.data.put(EntityDataKeys.leastTries, this.playerLeastTries);
                }
            }

            Util.log("\nWelcome to the guessing game! You have " + playerChances + " chances.");
            Util.log("The guessing range is [" + this.start + ", " + this.range + "]");
        }
    }

    @Override @SuppressWarnings("unchecked") public void update() {
        if (!this.mixed) {
            if (this.guess(this.playerChances)) {
                this.entity.data.put(EntityDataKeys.hasWon, true);

                // If player did better than before
                if (this.playerTries < this.playerLeastTries) {
                    this.entity.data.put(EntityDataKeys.leastTries, this.playerTries);
                }
            } else {
                this.entity.data.put(EntityDataKeys.hasWon, false);
            }
        } else {
            if (this.guessOnce((Hashtable<Object, Object>) this.enterParams.get(EntityDataKeys.guessParams))) {
                this.entity.data.put(EntityDataKeys.hasWon, true);
            } else {
                this.entity.data.put(EntityDataKeys.hasWon, false);
            }
        }
        
        // Set necessary data
        if (this.mixed || this.multiPlayer) {
            // As many chances as tries
            this.entity.data.put(EntityDataKeys.chances, playerTries);
            this.entity.data.put(EntityDataKeys.tries, playerTries);
        } else {
            this.entity.data.put(EntityDataKeys.chances, playerChances);
            this.entity.data.put(EntityDataKeys.tries, playerTries);

            // If Player has won, increment the number of wins, else increment the number of losses
            if (this.entity.data.get(EntityDataKeys.hasWon).equals(true)) {
                this.entity.data.put("numWins",
                    (Integer.parseInt(this.entity.data.get("numWins").toString()) + 1));
            } else {
                this.entity.data.put("numLosses",
                    (Integer.parseInt(this.entity.data.get("numLosses").toString()) + 1));
            }
        }

        // Change Player's state to PlayerIdle in TIME_WAIT seconds (or half of that if mixed)
        try {
            Util.log(this.mixed
                ? "+--------------------------------------------------------+\n"
                : "\nReturning in " + (Constants.TIME_WAIT_TO_RETURN / 1000.00) + " seconds...\n");
            Thread.sleep(this.mixed ? Constants.TIME_WAIT_TO_RETURN / 2 : Constants.TIME_WAIT_TO_RETURN);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Change Player's state to PlayerIdle
        this.entity.changeState(StateNames.PlayerIdle, new Hashtable<>() {{
            put(DataKeys.entity, entity);
            put(DataKeys.in, in);
        }});
    }

    @Override public void exit() {}

    /* Returns true if the user guesses correctly, false otherwise */
    public boolean guess(int n) {
        // Create an instance of Random
        Random r = this.seed == -1 ? new Random() : new Random(seed);
        // Pseudo-randomly generate target number
        int target = this.start + r.nextInt(this.range + 1 - this.start);

        this.entity.data.put(EntityDataKeys.target, target);
        
        for (int i = 0; i < n; i++) {    
            int guess = Util.getInt(in, "What is your guess: ");

            // Increment the number of tries
            this.playerTries++;
            
            // If guess too low, too high, or equal
            if (guess < target) {
                Util.log("Too low!");
            } else if (guess > target) {
                Util.log("Too high!");
            } else {
                Util.log("You have guessed the number (" + target + ") successfully in " + this.playerTries + " tries!");
                return true;
            }
        }
        
        // User failed to guess within `n` tries
        Util.log("You have lost, the target number was " + target + ".");
        return false;
    }

    public boolean guessOnce(Hashtable<Object, Object> guessParams) {
        // Unpack
        int target = Integer.parseInt(guessParams.get(EntityDataKeys.playerGuessTarget).toString());

        // Update data
        this.entity.data.put(EntityDataKeys.target, target);
        
        int guess = Util.getInt(in, "What is your guess: ");

        // Increment the number of tries
        this.playerTries++;
        
        // If guess too low, too high, or equal
        if (guess < target) {
            Util.log("Too low!");
        } else if (guess > target) {
            Util.log("Too high!");
        } else {
            if (this.multiPlayer) {
                Util.log("Player: " + this.entity.data.get(EntityDataKeys.name) + " have guessed the target number (" + target + ")!");
            } else {
                Util.log("The player have guessed the target number (" + target + ")!");
            }
            return true;
        }

        return false;
    }
}
