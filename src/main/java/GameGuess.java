public class GameGuess {
    private boolean couldHaveBeenEnciphered;
    private String enciphered;
    private String guess;

    GameGuess(boolean couldHaveBeenEnciphered, String enciphered, String guess) {
        this.couldHaveBeenEnciphered = couldHaveBeenEnciphered;
        this.enciphered = enciphered;
        this.guess = guess;
    }

    boolean couldHaveBeenEnciphered() {
        return this.couldHaveBeenEnciphered;
    }

    String getEnciphered() {
        return this.enciphered;
    }

    String getGuess() {
        return this.guess;
    }
    @Override
    public String toString() {
        return "[Could have been enciphered: " + couldHaveBeenEnciphered + ", Enciphered Value: " + enciphered + ", Player Guess: " + guess + "]";
    }
}
