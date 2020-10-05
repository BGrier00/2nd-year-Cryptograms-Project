import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class Game {

    private Cryptogram cryptogram;
    private String filepath;

    private boolean showLetterFrequency = false;

    private HashMap<String, Character> playerGameMapping = new HashMap<>(); // Cipher, Guess
    private int correctGuesses;
    private int incorrectGuesses;
    private ArrayList<String> charactersPlayed = new ArrayList<>();

    final static String ESCAPE_DELIMITER = "\\|";
    final static String DELIMITER = "|";

    private Player player;

    /**
     * Create a new game with the specified type
     *
     * @param cryptogramType Type of cryptogram to be played, letter or cryptogram
     * @param filepath       Path to our cryptogram puzzles
     */
    Game(Player player, String cryptogramType, String filepath) {
        try {
            this.player = player;
            this.filepath = filepath;
            this.cryptogram = generateCryptogram(cryptogramType);  // Make our cryptogram

        } catch (IllegalAccessException e) {
            System.out.println("Failed to create a new cryptogram. Please check the filepath provided exists: " + e);
            System.exit(20);
        }
    }

    /**
     * Try and resume a new game for the current player
     * @param player Current player
     */
    Game(Player player) throws FileNotFoundException {
        this.player = player;

        // Filepath for this users' saved game.
        String savedGameFilepath = "stateful/" + player.getUsername() + ".txt";

        // Try and load the game
        FileService fileService = new FileService(savedGameFilepath);
        ArrayList<String> game = fileService.read();

        // No saved game found
        if (game == null) { throw new FileNotFoundException("File not found"); }

        // Setup The Game
        this.cryptogram = generateCryptogram(game);
        if (this.cryptogram == null) { throw new FileNotFoundException("Invalid game type"); }
    }


    /**
     * Create a new cryptogram of the correct type
     *
     * @param cryptType Letter or number cryptogram
     * @return A new cryptogram
     * @throws IllegalAccessException Invalid type supplied
     */
    Cryptogram generateCryptogram(String cryptType) throws IllegalAccessException {
        if (cryptType.equals("letter")) {
            player.incrementCryptogramsPlayed();
            return new LetterCryptogram(filepath);

        } else if (cryptType.equals("number")) {
            player.incrementCryptogramsPlayed();
            return new NumberCryptogram(filepath);

        } else {
            throw new IllegalAccessException("Please enter a valid type: 'letter' or 'number'");
        }
    }

    /**
     * Create a new cryptogram built from a saved instance
     * @param game The array from which the cryptogram should be built
     * @return A new cryptogram
     */
    private Cryptogram generateCryptogram(ArrayList<String> game) {

        // Populate player guesses (Line 2 & 3)
        correctGuesses = Integer.parseInt(game.get(2));
        incorrectGuesses = Integer.parseInt(game.get(3));

        // charactersPlayed by the user (Line 7)
        if (game.get(7).contains(DELIMITER)) {
            ArrayList<String> charactersPlayedArr = new ArrayList<>(Arrays.asList(game.get(7).split(ESCAPE_DELIMITER)));
            charactersPlayed.addAll(charactersPlayedArr); // List of characters the user has made guesses for
        }

        // Player game mappings - Guesses that the user has already made {enciphered} > {deciphered}
        ArrayList<String> line6 = new ArrayList<>(Arrays.asList(game.get(6).split(ESCAPE_DELIMITER)));

        for (String guess:line6) {
            if (!guess.equals("")) {
                String[] guessParsed = guess.split(">"); // Split {enciphered}>{deciphered} to {enciphered} and {deciphered}
                this.playerGameMapping.put(guessParsed[0], guessParsed[1].charAt(0));
            }
        }

        // Create cryptogram object
        if (game.get(1).equals("LetterCryptogram")) {
            return new LetterCryptogram(game);

        } else if (game.get(1).equals("NumberCryptogram")) {
            return  new NumberCryptogram(game);

        } else {
            return null;
        }
    }


    ArrayList<String> getEncipheredPhrase() {
        return cryptogram.getPhrase();
    }

    ArrayList<GameGuess> getGuesses() {
        ArrayList<GameGuess> guesses = new ArrayList<>();

        for (String character : cryptogram.getPhrase()) {
            // Is this a character that could have been enciphered? If not, then just display its normal value.
            if (!cryptogram.getAlphabet().contains(character)) {
                guesses.add(new GameGuess(false, null, character));
                continue;
            }

            // Has the user already made a guess for this character? If so, display their guess.
            if (playerGameMapping.containsKey(character)) {
                guesses.add(new GameGuess(true, character, playerGameMapping.get(character).toString()));

            } else { // They've not yet made a guess.
                guesses.add(new GameGuess(true, character, "-"));
            }
        }
        return guesses;
    }

    /**
     * Check if the user has won the cryptogram
     * Have they provided a guess for each character?
     * Check each guess, if any guess is wrong, the user has not yet won the game
     */
    boolean hasWon() {
        // Check user has entered all letters.
        if (playerGameMapping.size() != cryptogram.getNumberEncipheredChars()) {
            return false;
        }
        // Check if the user has won the game.
        for (String c : cryptogram.getPhrase()) {
            try {
                if (playerGameMapping.containsKey(c)) {
                    if (playerGameMapping.get(c) != cryptogram.getPlainLetter(c)) {
                        return false; // At least one letter not correct
                    }
                }

                // Should never end up here.
            } catch (CharacterNotFoundException e) {
                System.out.println("That's odd, you shouldn't have ended up here!");
            }
        }
        // The player has won the game.
        player.deleteSavedGame();

        player.incrementCryptogramsCompleted();
        player.updateAccuracy();

        // Update the saved players file.
        Players p = new Players();
        p.updatePlayer(player);

        return true;
    }

    void enterLetter(String enciphered, Character guess) {
        charactersPlayed.add(enciphered);
        playerGameMapping.put(enciphered, guess); // Add this to the user guesses
        checkLetter(enciphered, guess); // Check if this guess was correct & update stats
    }

    /**
     * Check if the user is trying to undo a letter
     *
     * @param input The character they are currently guessing
     * @param guess The guess made for this character
     * @return If this is an undo move
     */
    boolean isUndoLetter(String input, Character guess) {
        if (playerGameMapping.containsKey(input) && playerGameMapping.get(input) == guess) {
            playerGameMapping.remove(input);
            return true;
        }
        return false;
    }

    /* Is it currently possible to undo a guess? */
    boolean canUndo() {
        return charactersPlayed.size() > 0;
    }

    void undo() {
        if (charactersPlayed.size() > 0) {
            playerGameMapping.remove(charactersPlayed.get(charactersPlayed.size() - 1));
            charactersPlayed.remove(charactersPlayed.size() - 1);
        }
    }

    void undoLetter(String s, char decryptedLetter){
        String key = null;
        if(charactersPlayed.size() > 0) {
            for(Map.Entry<String, Character> entry : playerGameMapping.entrySet()) {
                String key2 = entry.getKey();
                Character value = entry.getValue();
                if(decryptedLetter==value){
                    key = key2;
                }
            }
            playerGameMapping.get(s);
            playerGameMapping.remove(key);
            charactersPlayed.remove(key);
        }
    }

    /**
     * Check if the user made a correct guess, increment appropriate counter
     *
     * @param input The character for which they are making the guess
     * @param guess Their answer
     */
    private void checkLetter(String input, Character guess) {

        try {
            if (cryptogram.getPlainLetter(input) == guess) {
                player.incrementCorrectGuesses();
                player.incrementTotalGuesses();
                player.updateAccuracy();
                correctGuesses++;

            } else {
                incorrectGuesses++;
                player.incrementTotalGuesses();
                player.updateAccuracy();
            }

            // If the character does not exist, then it was obviously wrong
        } catch (CharacterNotFoundException e) {
            incorrectGuesses++;
            player.incrementTotalGuesses();
            player.updateAccuracy();
        }
    }


    /**
     * Has the user already used this character as a guess?
     *
     * @param guess The character already entered
     * @return if this guess has already been made
     */
    boolean hasGuessBeenUsed(Character guess) {
        return (playerGameMapping.containsValue(guess.toString().charAt(0)));
    }


    /* Takes in an ArrayList of Strings, and removes any duplicate values! Simple stuff. */
    ArrayList<String> removeDups(ArrayList<String> phrase){

        ArrayList<String> arrayList= new ArrayList<>();
        for(String s : phrase){

            if(!cryptogram.getAlphabet().contains(s)){
                continue;
            }
            if(!arrayList.contains(s)){
                arrayList.add(s);
            }
        }
        return arrayList;
    }

    ArrayList<String> removeDupNumbers(ArrayList<String> phrase){

        ArrayList<String> arrayList= new ArrayList<>();
        for(String s : phrase){
            if(!arrayList.contains(s)){
                arrayList.add(s);
            }
        }
        return arrayList;
    }




    String getHint() throws CharacterNotFoundException {

        ArrayList<String> phrase = cryptogram.getPhrase();               // ciphered
        ArrayList<String> alphabet = cryptogram.getAlphabet();
        Random random = new Random();
        int charValue = random.nextInt(phrase.size());                  // generate a random number up to size of phrase
        String encryptedLetter = phrase.get(charValue);                 // get a random letter from phrase
        char decryptedLetter = cryptogram.getPlainLetter(encryptedLetter);  // decipher it
        ArrayList<String> phraseWithoutDups;

        if(isNumeric(phrase.get(0))){
            phraseWithoutDups = removeDupNumbers(phrase);
        }else {
            phraseWithoutDups = removeDups(phrase);          //HELLOWORLD becomes HELOWRD
        }


        if (playerGameMapping == null) {
            enterLetter(encryptedLetter, decryptedLetter);
            return decryptedLetter + "";
        }

        if (!(alphabet.contains(encryptedLetter))) {        // if it's punctuation or another invalid character, try again
            return getHint();
        }

        // If your mapping already contains the encrypted letter, but there are empty slots, recurse
        // or if there are no empty guess slots, return null. This signals the hintHelp GUI.
        if (playerGameMapping.containsKey(encryptedLetter) && charactersPlayed.size() < phraseWithoutDups.size()) {
                    return getHint();
        }  else if (playerGameMapping.containsKey(encryptedLetter) && charactersPlayed.size() >= phraseWithoutDups.size()){
                    return null;
        }

        correctGuesses--;
        // If the user has already entered a letter it will be undone and later replaced.
        // eg    LE--O WOR-D    --> getHint() -->    -ELLO WORLD
        if(playerGameMapping.containsValue(decryptedLetter)){
            undoLetter(encryptedLetter, decryptedLetter);
        }
        enterLetter(encryptedLetter, decryptedLetter);
        return encryptedLetter + decryptedLetter;
    }


    public Cryptogram getCryptogram() {
        return cryptogram;
    }

    boolean showLetterFrequency() {
        return this.showLetterFrequency;
    }

    /**
     * Player has given up on the game
     * We will complete the game for them
     */
    void forfeit() throws CharacterNotFoundException {
        playerGameMapping = new HashMap<>(); // Reset player guesses.
        charactersPlayed = new ArrayList<>(); // Reset characters guessed.


        //Decipher phrase.
        for (String s:cryptogram.getPhrase()) {
            if (!playerGameMapping.containsKey(s) && !charactersPlayed.contains(s)) {
                playerGameMapping.put(s, cryptogram.getPlainLetter(s));
                charactersPlayed.add(s);
                player.incrementTotalGuesses();
            }
        }

        //Updates score when user forfeits.
        correctGuesses = 0;
        incorrectGuesses = charactersPlayed.size();

        // Delete saved game file if one exists.
        player.deleteSavedGame();

        // Update the saved players file.
        Players p = new Players();
        p.updatePlayer(player);
    }

    String showLetterFrequencyString() {
        if (showLetterFrequency) { return "On"; }
        return "Off";
    }

    void toggleShowLetterFrequency() {
        this.showLetterFrequency = !this.showLetterFrequency;
    }

    void saveGame() {
        cryptogram.saveCryptogram(this.player, this);

        //Saves players progress.
        Players p = new Players();
        p.updatePlayer(player);
    }


    void addPlayerGameMapping(String input, Character guess) {
        playerGameMapping.put(input, guess);
    }

    int getCorrectGuesses() {
        return this.correctGuesses;
    }

    int getIncorrectGuesses() {
        return this.incorrectGuesses;
    }

    HashMap<String, Character> getPlayerGameMapping() {
        return this.playerGameMapping;
    }

    ArrayList<String> getCharactersPlayed() {
        return this.charactersPlayed;
    }

    // Return current stats for this game.
    JPanel getGameStatsPnl() {
        JPanel playerStatsPnl = new JPanel(new GridLayout(5, 2));

        playerStatsPnl.add(new JLabel("Correct Guesses: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(correctGuesses), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Incorrect Guesses: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(incorrectGuesses), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Total Guesses: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(String.valueOf(correctGuesses + incorrectGuesses), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel("Accuracy: ", SwingConstants.RIGHT));
        playerStatsPnl.add(new JLabel(getAccuracy(), SwingConstants.LEFT));

        playerStatsPnl.add(new JLabel(""));
        playerStatsPnl.add(new JLabel(""));

        return playerStatsPnl;
    }

    private int getTotalGuesses() {
        return incorrectGuesses + correctGuesses;
    }

    String getAccuracy() {
        if (getTotalGuesses() == 0) { return "No Guesses"; }
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(((double) correctGuesses/ (double) getTotalGuesses())*100) + "%";
    }
    static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

