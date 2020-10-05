import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;

public class Player implements Comparator<Player>, Comparable<Player> {

    private String username;
    private int accuracy;
    private int correctGuesses;
    private int totalGuesses;
    private int cryptogramsPlayed;
    private int cryptogramsCompleted;

    public Player(String username, int accuracy, int totalGuesses, int correctGuesses, int cryptogramsPlayed, int cryptogramsCompleted) {
        this.username = username;
        this.accuracy = accuracy;
        this.correctGuesses = correctGuesses;
        this.totalGuesses = totalGuesses;
        this.cryptogramsPlayed = cryptogramsPlayed;
        this.cryptogramsCompleted = cryptogramsCompleted;
    }

    String getUsername() {
        return username;
    }

    String getAccuracy() {
        if (totalGuesses == 0) { return "0%"; }

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(((double) correctGuesses/ (double) totalGuesses)*100) + "%";
    }

    double getAccuracyNumeric() {
        if (totalGuesses == 0) {
            return 0;
        }

        return ((double) correctGuesses / (double) totalGuesses) * 100;
    }

    int getCorrectGuesses() { return correctGuesses;}

    int getTotalGuesses() {
        return totalGuesses;
    }

    void setTotalGuesses(int totalGuesses) {
        this.totalGuesses = totalGuesses;
    }

    int getCryptogramsPlayed() {
        return cryptogramsPlayed;
    }

    void setCryptogramsPlayed(int cryptogramsPlayed) {
        this.cryptogramsPlayed = cryptogramsPlayed;
    }

    int getCryptogramsCompleted() {
        return cryptogramsCompleted;
    }

    void setCryptogramsCompleted(int cryptogramsCompleted) {
        this.cryptogramsCompleted = cryptogramsCompleted;
    }

    // Update accuracy.
    void updateAccuracy(){
        accuracy = (correctGuesses / totalGuesses)*100;
    }

    // Increment cryptograms completed by 1.
    void incrementCryptogramsCompleted(){
        cryptogramsCompleted = cryptogramsCompleted + 1;
    }

    void incrementCryptogramsPlayed(){
        cryptogramsPlayed = cryptogramsPlayed + 1;
    }

    void incrementCorrectGuesses() { correctGuesses = correctGuesses + 1;}

    void incrementTotalGuesses() { totalGuesses = totalGuesses + 1; }

    boolean hasSavedGame() {
        FileService fileService = new FileService("stateful/" + username + ".txt");
        return fileService.exists();
    }

    void deleteSavedGame() {
        FileService fileService = new FileService("stateful/" + username + ".txt");
        fileService.deleteFile();
    }

    // Return current stats for this player
    JPanel getPlayerStatsPnl() {
        JPanel playerStatsPnl = new JPanel(new GridLayout(7, 2));

        playerStatsPnl.add(new JLabel("Username: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(username, SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Total Guesses: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(totalGuesses), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Correct Guesses: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(correctGuesses), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Accuracy: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(getAccuracy(), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Games Played: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(cryptogramsPlayed), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Games Completed: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(cryptogramsCompleted), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel(""));
        playerStatsPnl.add(new JLabel(""));

        return playerStatsPnl;
    }

    @Override
    public String toString() {
        return  username + Game.DELIMITER +
                accuracy + Game.DELIMITER +
                totalGuesses + Game.DELIMITER +
                correctGuesses + Game.DELIMITER +
                cryptogramsPlayed + Game.DELIMITER +
                cryptogramsCompleted + Game.DELIMITER ;
    }

    @Override
    public int compareTo(Player o) {
        return this.getUsername().compareTo(o.getUsername());
    }

    @Override
    public int compare(Player o1, Player o2) {
        return Integer.compare(o1.getCryptogramsCompleted(), o2.getCryptogramsCompleted());
    }
}
