package states.entity;

/* interfaces have every field public, static and final */
public interface EntityDataKeys {
    // Random keys
    String start = "start";
    String range = "range";
    String seed = "seed";
    
    // Player keys
    String states = "states";
    String name = "name";
    String newPlayer = "newPlayer";
    String hasWon = "hasWon";
    String target = "target";
    String tries = "tries";
    String chances = "chances";
    String leastTries = "leastTries";
}
