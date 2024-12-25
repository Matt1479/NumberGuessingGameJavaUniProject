package utility;

public class Settings {
    int start = Constants.START;
    int range = Constants.RANGE;
    int seed = Constants.SEED;

    int chances = Constants.CHANCES;

    // Getters
    public int getStart() {
        return start;
    }
    public int getRange() {
        return range;
    }
    public int getSeed() {
        return seed;
    }
    public int getChances() {
        return chances;
    }
    public String getDifficultyLevel() {
        switch (range) {
            case Constants.RANGE_EASY:
                return "Easy";
            
            case Constants.RANGE_NORMAL:
                return "Normal";
        
            case Constants.RANGE_HARD:
                return "Hard";
            
            default:
                return "Easy";
        }
    }

    // Setters
    public void setStart(int start) {
        this.start = start;
    }
    public void setRange(int range) {
        switch (range) {
            case Constants.RANGE_EASY:
                this.chances = Constants.CHANCES_EASY;
                break;
            
            case Constants.RANGE_NORMAL:
                this.chances = Constants.CHANCES_NORMAL;
                break;
        
            case Constants.RANGE_HARD:
                this.chances = Constants.CHANCES_HARD;
                break;
            
            default:
                this.chances = Constants.CHANCES_EASY;
                break;
        }
        this.range = range;
    }
    public void setSeed(int seed) {
        this.seed = seed;
    }
    public void setChances(int chances) {
        this.chances = chances;
    }
}
