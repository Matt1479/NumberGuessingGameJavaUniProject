package states.entity.player;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;


import states.StateNames;
import states.Util;
import states.entity.EntityBaseState;

public class PlayerGuessingState extends EntityBaseState {
    private Scanner in;

    private int playerChances;
    private int start;
    private int range;
    private long seed;

    // Keeps track of how many tries it took the user to guess the number, if they did
    private int playerTries;
    // Keeps track of the least tries it took a Player to win
    private int playerLeastTries;
    // Whether we want to greet the player and log their information...
    private boolean greetPlayer = false;

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.entity = (Player) enterParams.get("entity");
        this.in = (Scanner) enterParams.get("in");
        this.start = (int) enterParams.get("start");
        this.range = (int) enterParams.get("range");
        this.seed = (int) enterParams.get("seed");

        this.playerChances = (int) enterParams.get("chances");
        this.playerLeastTries = Integer.parseInt(this.entity.data.get("leastTries").toString());
        this.playerTries = 0;

        if (this.entity.data.get("newPlayer").equals(false) && greetPlayer) {
            Util.log("\nHello, " + this.entity.data.get("name") + "!");

            if (this.entity.data.containsKey("hasWon")) {
                if (this.entity.data.get("hasWon").toString().equals("true")) {
                    Util.log("You have won the last time!");
                } else {
                    Util.log("You haven't won the last time.");
                }
            }
            Util.log("Your least amount of tries to win: " + this.playerLeastTries);

            String choice = Util.getString(this.in, "Do you want to start over (y/n): ");
            if (Util.listContains(choice, Arrays.asList("yes", "y"))) {
                this.playerLeastTries = 10;
            }
        }

        Util.log("\nWelcome to the guessing game! You have " + playerChances + " chances.\n");
    }

    @Override public void update() {
        if (this.guess(this.playerChances)) {
            this.entity.data.put("hasWon", true);
            
            // If player did better than before
            if (this.playerTries < this.playerLeastTries) {
                this.entity.data.put("leastTries", this.playerTries);
            }
        } else {
            this.entity.data.put("hasWon", false);
        }
        
        // Set necessary data
        this.entity.data.put("chances", playerChances);
        this.entity.data.put("tries", playerTries);

        // Change states
        this.entity.changeState(StateNames.PlayerIdle, new Hashtable<>() {{
            put("entity", entity);
            put("in", in);
        }});
    }

    @Override public void exit() {}

    /* Returns true if the user guesses correctly, false otherwise */
    public boolean guess(int n) {
        // Create an instance of Random
        Random r = this.seed == -1 ? new Random() : new Random(seed);
        // Pseudo-randomly generate target number
        int target = this.start + r.nextInt(this.range + 1 - this.start);

        this.entity.data.put("target", target);
        
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
}
