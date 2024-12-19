package states;
import java.util.Hashtable;

public class StateMachine {
    private BaseState empty = new BaseState();
    private Hashtable<StateNames, BaseState> states;
    private BaseState current;

    // Init (constructor)
    public StateMachine(Hashtable<StateNames, BaseState> states) {
        this.states = states;
        this.current = this.empty;
    }

    public void add(StateNames stateName, BaseState state) {
        this.states.put(stateName, state);
    }

    public void remove(StateNames stateName) {
        this.states.remove(stateName);
    }

    public void clear() {
        this.states.clear();
    }

    public void change(StateNames stateName, Hashtable<Object, Object> enterParams) {
        // Make sure this state exists
        if (!this.states.containsKey(stateName)) {
            throw new IllegalArgumentException("State " + stateName + " does not exist");
        }

        // Exit current state
        this.current.exit();
        
        // Get a reference to new state
        this.current = this.states.get(stateName);

        // Add gStateMachine and stateName to enterParams dictionary
        if (enterParams != null) {
            enterParams.put("gStateMachine", this);
            enterParams.put("stateName", stateName);
        }

        // Enter new state
        this.current.enter(enterParams);
    }

    public void update() {
        this.current.update();
    }

    public void render() {
        this.current.render();
    }
}