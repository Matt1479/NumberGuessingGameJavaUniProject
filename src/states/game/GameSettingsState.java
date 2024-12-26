package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateMachine;
import states.StateNames;
import utility.Constants;
import utility.Settings;
import utility.Util;

public class GameSettingsState extends BaseState {
    private Settings settings;

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get(DataKeys.gStateMachine);
        this.in = (Scanner) enterParams.get(DataKeys.in);
        this.stateName = (StateNames) enterParams.get(DataKeys.stateName);

        this.settings = (Settings) enterParams.get(DataKeys.settings);
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.loadParams(enterParams);
    }

    @Override public void exit() {}

    @Override public void update() {
        Util.log("+------------------------Settings------------------------+");

        // Display options
        this.displayOptions();
        
        // Ask for choice
        int choice = Util.getInt(this.in, "Your choice: ");

        // Make sure the user's choice is valid (condition could be moved to Util.getInt())
        if (choice < 0 || choice > 3) {
            Util.log("Invalid choice. Please try again.");
            return;
        }

        switch (choice) {
            case 0:
                this.settings.setRange(Constants.RANGE_EASY, true);
                break;
            
            case 1:
                this.settings.setRange(Constants.RANGE_NORMAL, true);
                break;
            
            case 2:
                this.settings.setRange(Constants.RANGE_HARD, true);
                break;

            case 3:
                // Prompt for customRange
                int customStart = Util.getInt(this.in, "Input custom start: ");

                // Prompt for customRange
                int customRange = Util.getInt(this.in, "Input custom limit: ");
                while (customRange <= customStart) {
                    Util.log("Limit can't be less than or equal to start");
                    customRange = Util.getInt(this.in, "Input custom limit: ");
                }

                // Compute length
                int length = customRange - customStart;

                // Set difficulty and number of chances accordingly
                if (length <= 1) {
                    this.settings.setChances(1);
                } else if (length <= 5) {
                    this.settings.setChances(2);
                } else if (length <= 10) {
                    this.settings.setChances(3);
                } else if (length <= 50) {
                    this.settings.setChances(5);
                } else if (length <= 75) {
                    this.settings.setChances(7);
                } else if (length <= 99) {
                    this.settings.setChances(9);
                }
                else {
                    if (length <= Constants.RANGE_EASY) {
                        this.settings.setChances(Constants.CHANCES_EASY);
                    } else if (length <= Constants.RANGE_NORMAL) {
                        this.settings.setChances(Constants.CHANCES_NORMAL);
                    } else if (length <= Constants.RANGE_HARD) {
                        this.settings.setChances(Constants.CHANCES_HARD);
                    } else {
                        this.settings.setChances(100);
                    }
                }

                // Set necessary data
                this.settings.setRange(customRange, false);
                this.settings.setStart(customStart);
                this.settings.setDifficultyLevel("Custom");

                break;
            
            default:
                this.settings.setRange(Constants.RANGE_EASY, true);
                break;
        }

        // Change to PlayState, passing (Scanner) in, and settings
        this.gStateMachine.change(StateNames.GamePlay, new Hashtable<>() {{
            put(DataKeys.in, in);
            put(DataKeys.settings, settings);
        }});

        Util.log("\n+--------------------------------------------------------+");
    }

    public void displayOptions() {
        Util.log("\nDifficulty levels:\n");
        int i = 0;
        Util.log(i + ". " + "Easy");    i++;
        Util.log(i + ". " + "Normal");  i++;
        Util.log(i + ". " + "Hard");    i++;
        Util.log(i + ". " + "Advanced (custom)");  i++;
    }
}
