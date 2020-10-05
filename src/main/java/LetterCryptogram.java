import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LetterCryptogram extends Cryptogram {

    /**
     * Generate a new Letter cryptogram from a saved game file
     * @param game ArrayList from which to build a game
     */
    LetterCryptogram(ArrayList<String> game) {

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

        // Enciphered phrase (Line 4)
        phrase = new ArrayList<>(Arrays.asList(game.get(4).split(Game.ESCAPE_DELIMITER)));

        // Enciphered mappings {enciphered} > {deciphered} (Line 5)
        cryptogramAlphabet = new HashMap<>(); // Enciphered > Deciphered
        ArrayList<String> line5 = new ArrayList<>(Arrays.asList(game.get(5).split(Game.ESCAPE_DELIMITER)));
        for (String mapping:line5) {
            String[] mappingParsed = mapping.split(">"); // Split {enciphered}>{deciphered} to {enciphered} and {deciphered}

            if (!cryptogramAlphabet.containsKey(mappingParsed[0])) { // Check it does not already exist in the mapping
                cryptogramAlphabet.put(mappingParsed[0], mappingParsed[1].charAt(0));

                if (ALPHABET.contains(mappingParsed[1])) {
                    numberEncipheredChars++; // Number of uniquely enciphered characters
                }
            }
        }
    }

    /**
     * Generate a new cryptogram from an existing file file
     * @param file File from which to open cryptogram
     */
    LetterCryptogram(String file) {
        // Choose a phrase from the file, this is deciphered
        ArrayList<Character> deciphered = loadCryptogramFile(file);

        // Generate encrypted cryptogram - Use a HashMap to map an encrypted letter to it's real value
        cryptogramAlphabet = encipherCryptogram(deciphered);
    }


    /**
     * Select a unique character to use as and encrypted value
     * @param cryptogram Current mapping from enciphered to deciphered characters
     * @return A unique character not already in the cryptogram mapping
     */
    public String encipherCharacter(HashMap<String, Character> cryptogram) {
        int MAX = ALPHABET.length() - 1;

        // Choose a random character to map to from the alphabet
        String enciphered = String.valueOf(ALPHABET.charAt(getRandomNumber(0, MAX)));

        // Check this character does not already have a mapping
        while (cryptogram.containsKey(enciphered)) {
            enciphered = String.valueOf(ALPHABET.charAt(getRandomNumber(0, MAX)));
        }

        return enciphered;
    }


    public ArrayList<String> getAlphabet() {
        char[] temp = ALPHABET.toCharArray();

        ArrayList<String> alphabet = new ArrayList<>();

        for (char c:temp) {
            alphabet.add(String.valueOf(c));
        }

        return alphabet;
    }
}
