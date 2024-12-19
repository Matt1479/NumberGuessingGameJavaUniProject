package states;

import java.util.Hashtable;
import java.util.Scanner;

public class BaseState extends Object {
    // Protected: children can use it as if it's public
    protected Hashtable<Object, Object> enterParams;
    protected StateMachine gStateMachine;
    protected Scanner in;
    protected Object stateName;

    // Init/Constructor
    // public BaseState() {}

    public void enter(Hashtable<Object, Object> enterParams) {}
    public void exit() {}
    public void update() {}
    public void render() {}
}
