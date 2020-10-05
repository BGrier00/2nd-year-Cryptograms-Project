import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LetterCryptogramTest {

    private Cryptogram cryptogram;

    @BeforeEach
    void setUp() {
        cryptogram = new LetterCryptogram("games/cryptograms-test.txt"); // Create a cryptogram from our test file
        ArrayList<String> phrase = cryptogram.getPhrase();
    }


    /* Provided a HashMap of values, return a unique character not already in the HashMap */
    @Test
    void encipherCharacter() {
        HashMap<String, Character> existingMappings = new HashMap<>();

        // Add uppercase
        for (int i = 65; i <= 90; i++) {
            existingMappings.put(String.valueOf((char) i), (char) i);
        }

        // Add lowercase
        for (int i = 97; i <= 122; i++) {
            existingMappings.put(String.valueOf((char) i), (char) i);
        }

        String remove = "G"; // The element we will remove from the list and that the function should return
        existingMappings.remove(remove); // Remove a single key, this is what the function should return
        assertEquals(remove, cryptogram.encipherCharacter(existingMappings)); // get a new number from the function
    }


    /* Will return an array of characters which could be used to encrypt the phrase */
    @Test
    void getAlphabet() {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        ArrayList<String> confirmedAlphabet = new ArrayList<>();

        for (int i = 0; i < ALPHABET.length(); i++){
            confirmedAlphabet.add(String.valueOf(ALPHABET.charAt(i)));
        }

        assertEquals(confirmedAlphabet, cryptogram.getAlphabet());
    }
}