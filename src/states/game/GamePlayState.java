package states.game;

import java.util.Hashtable;
import java.util.Scanner;

import states.BaseState;
import states.StateMachine;
import states.StateNames;
import states.Util;
import states.entity.player.Player;
import states.entity.player.PlayerBaseState;

public class GamePlayState extends BaseState {
    Player p;

    // Methods
    public void loadParams(Hashtable<Object, Object> enterParams) {
        this.enterParams = enterParams;
        this.gStateMachine = (StateMachine) enterParams.get("gStateMachine");
        this.in = (Scanner) enterParams.get("in");
        this.stateName = (StateNames) enterParams.get("stateName");
    }

    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.loadParams(enterParams);

        // this.e = new Entity(null);
        // this.e.addState(StateNames.EntityBase, new EntityBaseState());
        // this.e.changeState(StateNames.EntityBase, new Hashtable<>() {{
        //     put("entity", e);
        // }});

        this.p = new Player(null);
        this.p.addState(StateNames.PlayerBase, new PlayerBaseState());
        this.p.changeState(StateNames.PlayerBase, new Hashtable<>() {{
            put("entity", p);
        }});
    }

    @Override public void exit() {}

    @Override public void update() {
        this.p.update();
    }
}
