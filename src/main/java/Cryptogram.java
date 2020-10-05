import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

abstract class Cryptogram {

    final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Available alphabet - Characters that we can encrypt
    static ArrayList<String> phrase; // enciphered phrase
    public String solution;
    private static final String filepath = "src/main/resources/"; // Path to cryptogram file
    int numberEncipheredChars; // Number of distinct characters enciphered in this cryptogram
    HashMap<String, Character> cryptogramAlphabet; // Mapping from phrase to enciphered

    abstract public ArrayList<String> getAlphabet();
    protected abstract String encipherCharacter(HashMap<String, Character> cryptogram);


    /**
     * Load all cryptograms from the file and choose a random cryptogram to play
     * @return New deciphered cryptogram
     */
    ArrayList<Character> loadCryptogramFile(String filename) {

        // Load all cryptograms from file
        ArrayList<String> cryptograms = loadCryptograms(filename);

        // Ensure cryptograms loaded correctly
        if (cryptograms == null) { return null; }

        // Choose random cryptogram
        int random = getRandomNumber(0, cryptograms.size() - 1);
        String cryptogramString = cryptograms.get(random).toUpperCase(); // Convert to uppercase

        // Convert String to ArrayList of characters
        ArrayList<Character> cryptogram = new ArrayList<>();
        CharacterIterator it = new StringCharacterIterator(cryptogramString);

        while (it.current() != CharacterIterator.DONE) {
            cryptogram.add(it.current());
            it.next();
        }

        return cryptogram;
    }


    /**
     * Load all cryptogram(s) from given file into an array list
     * NOTE: If opening a users' saved cryptogram, then there will only be one item in the ArrayList
     * @param filename File where cryptogram(s) are stored
     * @return ArrayList of cryptograms from this file
     */
    ArrayList<String> loadCryptograms(String filename) {
        FileService fileService = new FileService(filename);
        return fileService.read();
    }


    /**
     * Return the unencrypted value of this character
     * @param cryptoLetter The encrypted character
     * @return Decrypted character
     */
    char getPlainLetter(String cryptoLetter) throws CharacterNotFoundException {

        if (!getCryptogramAlphabet().containsKey(cryptoLetter)) {
            throw new CharacterNotFoundException(cryptoLetter);
        }

        return getCryptogramAlphabet().get(cryptoLetter);
    }

    /**
     * Generate a random number between two given values
     * @param min Minimum value
     * @param max Maximum value
     * @return A random number between min & max
     */
    int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * @return Enciphered phrase as a
     */

   //  String displayDecryptedPhrase(){}

    String displayEncryptedPhrase() {
        StringBuilder response = new StringBuilder();

        for (Object character : getPhrase()) {
            response.append(character);

            if (getClass() == NumberCryptogram.class) {
                response.append(" ");
            }
        }

        return response.toString();
    }

    /* Return number of distinct characters enciphered */
    int getNumberEncipheredChars(){
        return this.numberEncipheredChars;
    }

    /**
     * Encipher a phrase and return the mappings of the numbers as a HashMap
     * CryptogramReverse is used to keep a temp record of what deciphered numbers have already been used
     *
     * @param phraseToEncrypt Array of numbers that make up the deciphered phrase
     * @return A mapping from enciphered to deciphered characters
     */
    HashMap<String, Character> encipherCryptogram(ArrayList<Character> phraseToEncrypt) {
        HashMap<String, Character> cryptogram = new HashMap<>(); // Enciphered > Deciphered
        HashMap<Character, String> cryptogramReverse = new HashMap<>(); // Deciphered > Enciphered

        phrase = new ArrayList<>(); // Encrypted Phrase

        // For each character, assign it an enciphered value
        for (Character c:phraseToEncrypt) {

            // Ensure this does not already exist
            if (cryptogram.containsValue(c)) {
                phrase.add(cryptogramReverse.get(c));
                continue;
            }

            // We will only modify what we consider to be a character
            if (ALPHABET.indexOf(c) == -1) {
                cryptogram.put(String.valueOf(c), c);
                cryptogramReverse.put(c, String.valueOf(c));
                phrase.add(String.valueOf(c));
                continue;
            }

            // Generate a random character to map this to
            String enciphered = encipherCharacter(cryptogram);

            // Add this to our mappings
            cryptogram.put(enciphered, c); // Enciphered > Actual Value
            cryptogramReverse.put(c, enciphered);
            phrase.add(enciphered);

            // Increment number of distinct characters enciphered
            numberEncipheredChars++;
        }

        return cryptogram;
    }

    /**
     * @return Encrypted phrase
     */
    ArrayList<String> getPhrase() {
        return phrase;
    }


    /**
     * @return Number of times each character is used in cryptogram
     */
    HashMap<String, Integer> getFrequencies() {
        HashMap<String, Integer> frequencies = new HashMap<>();

        for (String c:getPhrase()) {
            if (frequencies.containsKey(c)) {
                frequencies.put(c, frequencies.get(c) + 1);
            } else {
                frequencies.put(c, 1);
            }
        }

        return frequencies;
    }


    /**
     * @return Mapping from enciphered character to deciphered character
     */
    HashMap<String, Character> getCryptogramAlphabet() {
        return cryptogramAlphabet;
    }


    /**
     * Save the current state of the cryptogram to a file
     * @param player The current player
     * @param game The current game instance
     */
    void saveCryptogram(Player player, Game game) {

        // Filepath to where the game should be saved
        String filename = "stateful/" + player.getUsername() + ".txt"; // Unique identifier for the player

        /*
        File Format will be:
        [Line 0] {Username}
        [Line 1] Cryptogram Type {Class name}
        [Line 2] Number of {correct guesses}
        [Line 3] Number of {incorrect guesses}
        [Line 4] Enciphered {phrase}, pipe separated
        [Line 5] {Enciphered} > {Deciphered} Mapping, pipe separated. Actual Mapping
        [Line 6] {Enciphered} > {Guess} Mapping, pipe separated. Player Guesses
        [Line 7] {charactersPlayed} by the player, pipe separated. Used to undo a move
        */

        ArrayList<String> output = new ArrayList<>();

        output.add(player.getUsername()); // Line 0
        output.add(getClass().getName()); // Line 1
        output.add(String.valueOf(game.getCorrectGuesses())); // Line 2
        output.add(String.valueOf(game.getIncorrectGuesses())); // Line 3

        // Enciphered phrase, pipe separated
        output.add(arrayToString(phrase)); // Line 4

        // {Enciphered} > {Deciphered} Mapping
        output.add(arrayToMappedString(phrase)); // Line 5

        // {Enciphered} > {Guess} Mapping
        output.add(playerGameMapping(game)); // Line 6

        // Characters used {character}|...
        output.add(arrayToString(game.getCharactersPlayed())); // Line 7

        // Save object to file
        FileService fileService = new FileService(filename);
        System.out.println("Saved to file: " + fileService.save(output));
    }

    /**
     * Takes in a mapping of enciphered > deciphered guesses
     * @param game Current game instance
     * @return string formatted as {enciphered}>{deciphered}|... (Pipe separated)
     */
    private String playerGameMapping(Game game) {
        StringBuilder guesses = new StringBuilder();
        HashMap<?, Character> mapping = game.getPlayerGameMapping();
        for (String p:phrase) {
            if (mapping.containsKey(p)) {
                guesses.append(p).append(">").append(mapping.get(p)).append(Game.DELIMITER);
                mapping.remove(p);
            }
        }
        return guesses.toString();
    }


    /**
     * @return array of all letters from the alphabet
     */
    static ArrayList<Character> getAlphabetAsArray() {
        ArrayList<Character> letters = new ArrayList<>();

        for (int i = 0; i < ALPHABET.length(); i++) {
            letters.add(ALPHABET.charAt(i));
        }

        return letters;
    }

    /**
     *
     * @param phrase Enciphered phrase
     * @return String of the format {Enciphered}>{Deciphered}|... (Pipe separated)
     */
    private String arrayToMappedString(ArrayList<String> phrase) {
        StringBuilder output = new StringBuilder();

        for (String p:phrase) {
            try {
                output.append(p).append(">").append(getPlainLetter(p)).append(Game.DELIMITER);
            } catch (CharacterNotFoundException e) {
                output.append(p).append(">").append(p).append(Game.DELIMITER);
            }
        }

        return output.toString();
    }

    /**
     * @param array Array of string values to be converted to a String
     * @return Pipe separated string
     */
    String arrayToString(ArrayList<String> array) {

        StringBuilder output = new StringBuilder();

        for (String s : array) {
            output.append(s).append(Game.DELIMITER);
        }

        return output.toString();
    }
}
