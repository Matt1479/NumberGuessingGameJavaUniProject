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

    // Whether it is the mixed gameplay
    boolean mixed;

    // Methods
    public void unpack(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.entity = (Program) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);

        this.start = (int) enterParams.get(EntityDataKeys.start);
        this.range = (int) enterParams.get(EntityDataKeys.range);
        this.seed = (int) enterParams.get(EntityDataKeys.seed);

        this.programChances = (int) enterParams.get(EntityDataKeys.chances);
        this.programLeastTries = Integer.parseInt(this.entity.data.get(EntityDataKeys.leastTries).toString());

        this.mixed = enterParams.containsKey("guessParams");

        this.programTries = this.mixed
            ? Integer.parseInt(this.entity.data.get(EntityDataKeys.tries).toString())
            : 0;
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.unpack(enterParams);

        if (this.mixed) {
            Util.log("+--------------------------------------------------------+");
            Util.log("Program's turn!\n");
        } else {
            if (this.entity.data.get(EntityDataKeys.newPlayer).equals(false)) {
                Util.log("The program's least amount of tries to win: " + this.programLeastTries);
            }
        }
    }

    @Override @SuppressWarnings("unchecked") public void update() {
        if (!this.mixed) {
            if (this.guess(this.programChances)) {
                this.entity.data.put(EntityDataKeys.hasWon, true);
                
                // If program did better than before
                if (this.programTries < this.programLeastTries) {
                    this.entity.data.put(EntityDataKeys.leastTries, this.programTries);
                }
            } else {
                this.entity.data.put(EntityDataKeys.hasWon, false);
            }
        } else {
            if (this.guessOnce((Hashtable<Object, Object>) this.enterParams.get("guessParams"))) {
                this.entity.data.put(EntityDataKeys.hasWon, true);
            } else {
                this.entity.data.put(EntityDataKeys.hasWon, false);
            }
        }
        
        // Set necessary data
        if (mixed) {
            // As many chances as tries
            this.entity.data.put(EntityDataKeys.chances, programTries);
            this.entity.data.put(EntityDataKeys.tries, programTries);
        } else {
            this.entity.data.put(EntityDataKeys.chances, programChances);
            this.entity.data.put(EntityDataKeys.tries, programTries);
        }
        
        // Change Program's state to ProgramIdle in TIME_WAIT seconds (or half of that if mixed)
        try {
            Util.log(this.mixed
                ? "+--------------------------------------------------------+\n"
                : "\nReturning in " + (Constants.TIME_WAIT_TO_RETURN / 1000.00) + " seconds...\n");
            Thread.sleep(this.mixed ? Constants.TIME_WAIT_TO_RETURN / 2 : Constants.TIME_WAIT_TO_RETURN);
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

            if (this.programTries != this.programChances) {
                Util.log("generating random int in the range of " + this.start + " to " + this.range + '\n');
            }
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

    /* Returns true if the program guesses correctly, false otherwise */
    public boolean guessOnce(Hashtable<Object, Object> guessParams) {
        // Unpack
        int target = (int) guessParams.get("programGuessTarget");
        int programGuess = (int) guessParams.get("programGuess");

        // Update data
        this.entity.data.put(EntityDataKeys.target, target);
        
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
            Util.log("The program have guessed the number (" + target + ")!");
            return true;
        }

        Util.log("generating random int in the range of " + this.start + " to " + this.range);
        this.entity.data.put(EntityDataKeys.start, this.start);
        this.entity.data.put(EntityDataKeys.range, this.range);

        try {
            Thread.sleep(Constants.GUESS_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return false;
    }
}