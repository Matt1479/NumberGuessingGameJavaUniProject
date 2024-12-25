package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.DataKeys;
import states.StateMachine;
import states.StateNames;
import utility.Settings;

public class GameStartState extends BaseState {
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

        // Change to SettingsState, passing (Scanner) in
        this.gStateMachine.change(StateNames.GameSettings, new Hashtable<>() {{
            put(DataKeys.in, in);
            put(DataKeys.settings, settings);
        }});
    }

    @Override public void exit() {}

    @Override public void update() {}
}
