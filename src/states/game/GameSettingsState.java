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

        this.settings = (Settings) enterParams.get("settings");
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
        if (choice < 0 || choice > 2) {
            Util.log("Invalid choice. Please try again.");
            return;
        }

        switch (choice) {
            case 0:
                this.settings.setRange(Constants.RANGE_EASY);
                break;
            
            case 1:
                this.settings.setRange(Constants.RANGE_NORMAL);
                break;
            
            case 2:
                this.settings.setRange(Constants.RANGE_HARD);
                break;
            
            default:
                this.settings.setRange(Constants.RANGE_EASY);
                break;
        }

        // Change to PlayState, passing (Scanner) in, and settings
        this.gStateMachine.change(StateNames.GamePlay, new Hashtable<>() {{
            put(DataKeys.in, in);
            put("settings", settings);
        }});

        Util.log("\n+--------------------------------------------------------+");
    }

    public void displayOptions() {
        Util.log("\nDifficulty levels:\n");
        int i = 0;
        Util.log(i + ". " + "Easy");    i++;
        Util.log(i + ". " + "Normal");  i++;
        Util.log(i + ". " + "Hard");    i++;
    }
}
