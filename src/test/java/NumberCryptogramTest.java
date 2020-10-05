import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NumberCryptogramTest {

    private Cryptogram cryptogram;

    @BeforeEach
    void setUp() {
        cryptogram = new NumberCryptogram("games/cryptograms-test.txt"); // Create a cryptogram from our test file
        ArrayList<String> phrase = cryptogram.getPhrase();
    }


    /* Provided a HashMap of values, generate a unique integer not already in the HashMap */
    @Test
    void encipherCharacter() {
        HashMap<String, Character> existingMappings = new HashMap<>();

        for (int i = 10; i <= 99; i++) {
            existingMappings.put(String.valueOf(i), 'a');
        }

        String remove = "50"; // The element we will remove from the list and that the function should return
        existingMappings.remove(remove); // Remove a single key, this is what the function should return
        assertEquals(remove, cryptogram.encipherCharacter(existingMappings)); // get a new number from the function
    }


    /* Will return an array of numbers which could be used to encrypt the phrase */
    @Test
    void getAlphabet() {
        ArrayList<String> confirmedAlphabet = new ArrayList<>();

        for (int i = 10; i < 100; i++){
            confirmedAlphabet.add(String.valueOf(i));
        }

        assertEquals(confirmedAlphabet, cryptogram.getAlphabet());
    }
}
