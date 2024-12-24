package states.entity.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import states.DataKeys;
import states.entity.Entity;
import states.entity.EntityBaseState;
import states.entity.EntityDataKeys;
import utility.Constants;
import utility.Util;

public class PlayerSaveState extends EntityBaseState {
    @Override public void enter(Hashtable<Object, Object> enterParams) {
        this.entity = (Entity) enterParams.get(DataKeys.entity);
        this.in = (Scanner) enterParams.get(DataKeys.in);

        // Save player data to file
        if (!this.savePlayerData()) {
            Util.log("Could not save player data.");
        }
    }

    public boolean savePlayerData() {
        Path filePath;
        if (this.entity.data.containsKey(EntityDataKeys.name)) {
            filePath = Paths.get("./" + this.entity.data.get(EntityDataKeys.name) + ".txt");
        } else {
            filePath = Paths.get("./" + Constants.DEFAULT_NAME + ".txt");
        }
        File f = new File(filePath.toString());

        // Overwrite file contents
        try (FileWriter fWriter = new FileWriter(f, false)) {
            for (Map.Entry<Object, Object> entry : this.entity.data.entrySet()) {
                fWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            return false;
        }

        // Success
        return true;
    }
}
