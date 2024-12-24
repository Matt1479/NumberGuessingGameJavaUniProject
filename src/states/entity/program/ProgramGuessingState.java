package states.entity.program;

import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import states.DataKeys;
import states.StateNames;
import states.entity.EntityBaseState;
import states.entity.EntityDataKeys;
import utility.Constants;
import utility.Util;

public class ProgramGuessingState extends EntityBaseState {
    private Scanner in;

    private int programChances;
    private int start;
    private int range;
    private long seed;

    // Keeps track of how many tries it took the program to guess the number, if it did
    private int programTries;
    // Keeps track of the least tries it took the program to win
    private int programLeastTries;

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.entity = (Program) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);

        this.start = (int) enterParams.get(EntityDataKeys.start);
        this.range = (int) enterParams.get(EntityDataKeys.range);
        this.seed = (int) enterParams.get(EntityDataKeys.seed);

        this.programChances = (int) enterParams.get(EntityDataKeys.chances);
        this.programLeastTries = Integer.parseInt(this.entity.data.get(EntityDataKeys.leastTries).toString());
        this.programTries = 0;

        if (this.entity.data.get(EntityDataKeys.newPlayer).equals(false)) {
            Util.log("The program's least amount of tries to win: " + this.programLeastTries);
        }
    }

    @Override public void update() {
        if (this.guess(this.programChances)) {
            this.entity.data.put(EntityDataKeys.hasWon, true);
            
            // If program did better than before
            if (this.programTries < this.programLeastTries) {
                this.entity.data.put(EntityDataKeys.leastTries, this.programTries);
            }
        } else {
            this.entity.data.put(EntityDataKeys.hasWon, false);
        }
        
        // Set necessary data
        this.entity.data.put(EntityDataKeys.chances, programChances);
        this.entity.data.put(EntityDataKeys.tries, programTries);

        // Change Program's state to ProgramIdle in TIME_WAIT seconds
        try {
            Util.log("\nReturning in " + (Constants.TIME_WAIT_TO_RETURN / 1000.00) + " seconds...\n");
            Thread.sleep(Constants.TIME_WAIT_TO_RETURN);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Change Program's state to ProgramIdle
        this.entity.changeState(StateNames.ProgramIdle, new Hashtable<>() {{
            put(DataKeys.entity, entity);
            put(DataKeys.in, in);
        }});
    }

    @Override public void exit() {}

    /* Returns true if the program guesses correctly, false otherwise */
    public boolean guess(int n) {
        // Create an instance of Random
        Random r = this.seed == -1 ? new Random() : new Random(seed);
        // Pseudo-randomly generate target number
        int target = this.start + r.nextInt(this.range + 1 - this.start);
        // Pseudo-randomly generate the program's guess
        int programGuess = this.start + r.nextInt(this.range + 1 - this.start);

        this.entity.data.put(EntityDataKeys.target, target);
        
        for (int i = 0; i < n; i++) {
            Util.log("Program's guess is: " + programGuess);

            // Increment the number of tries
            this.programTries++;
            
            // If guess too low, too high, or equal
            if (programGuess < target) {
                Util.log("Too low, ", false);
                this.start = programGuess + 1;
            } else if (programGuess > target) {
                Util.log("Too high, ", false);
                this.range = programGuess - 1;
            } else {
                Util.log("The program have guessed the number (" + target + ") successfully in " + this.programTries + " tries!");
                return true;
            }

            Util.log("generating random int in the range of " + this.start + " to " + this.range + '\n');
            programGuess = this.start + r.nextInt(this.range + 1 - this.start);

            try {
                Thread.sleep(Constants.GUESS_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Program failed to guess within `n` tries
        Util.log("The program did not manage to guess the target number: " + target + ".");
        return false;
    }
}
