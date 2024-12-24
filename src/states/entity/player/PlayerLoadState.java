package states.entity.player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Scanner;

import states.DataKeys;
import states.entity.Entity;
import states.entity.EntityBaseState;
import states.entity.EntityDataKeys;
import utility.Constants;
import utility.Util;

public class PlayerLoadState extends EntityBaseState {
    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);

        // Load player data from file, if it exists
        this.loadPlayerData();
    }

    public void loadPlayerData() {
        String name = Util.getString(in, "What is your name: ");
        name = name.equals("") ? Constants.DEFAULT_NAME : name;

        Path filePath = Paths.get("./" + name + ".txt");
        File f = new File(filePath.toString());

        // If a player (file) exists already
        if (f.exists()) {
            // Try reading data from file
            try (Scanner fReader = new Scanner(f)) {
                while (fReader.hasNextLine()) {
                    String[] lineArr = fReader.nextLine().split(":");
                    this.entity.data.put(lineArr[0], lineArr[1]);
                }
            } catch (Exception e) {
                Util.log("Error reading from file: " + e.getMessage());
            }

            // Add this lastly to overwrite whatever was in the file
            this.entity.data.put(EntityDataKeys.newPlayer, false);
        } else {
            // Player/file does not exist
            this.entity.data.put(EntityDataKeys.name, name);
            this.entity.data.put(EntityDataKeys.newPlayer, true);
            this.entity.data.put(EntityDataKeys.leastTries, Constants.START);
        }
    }
}
