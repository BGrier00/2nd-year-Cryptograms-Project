import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class CryptogramTest {
    private static final String TEST_FILE = "games/cryptograms-test.txt";
    private static final String EXPECTED_PHRASE = "This is a test!";

    private Cryptogram cryptogram;
    private ArrayList<String> phrase;

    @BeforeEach
    void setUp() {
        cryptogram = new LetterCryptogram(TEST_FILE);
        phrase = cryptogram.getPhrase();
    }


    /* Should return an arrayList of characters from our encrypted phrase */
    @Test
    void getPhrase() {
        assertEquals(15, phrase.size());
    }

    /* Should return a HashMap mapping each distinct encrypted integer to it's deciphered character */
    @Test
    void getCryptogramAlphabet() {
        // Get each distinct Character from the phrase
        ArrayList<String> distinctIntegers = new ArrayList<>();

        for (String o : phrase) {
            if (!distinctIntegers.contains(o)) {
                distinctIntegers.add(o);
            }
        }

        // Ensure each value is exists in the HashMap
        HashMap<String, Character> alphabet = cryptogram.getCryptogramAlphabet();

        // Ensure same size
        assertEquals(distinctIntegers.size(), alphabet.size());

        // Ensure we have a mapping for each character
        for (String i:distinctIntegers) {
            assertTrue(alphabet.containsKey(i));
        }
    }


    /**
     * Ensure we are correctly counting the frequency of each integer
     * Note: As the cipher is random each time we have first have to get the original character value
     */
    @Test
    void getFrequencies() {
        HashMap<String, Integer> frequencies = cryptogram.getFrequencies();

        for (String o:phrase) {

            // Count number of times this integer is used
            int occourences = 0;

            for (String p:phrase) {
                if (p.equals(o)) {
                    occourences++;
                }
            }

            Assertions.assertEquals(occourences, (int) frequencies.get(o));
        }
    }

    /* Count how many distinct characters have been enciphered */
    @Test
    void getNumberEncipheredChars() {
        assertEquals(6, cryptogram.getNumberEncipheredChars());
    }

    /* Should return an array of characters, containing "This is a test!" */
    @Test
    void loadCryptogramFile() {
        ArrayList<Character> expected = new ArrayList<>();

        char[] expectedPhrase = EXPECTED_PHRASE.toUpperCase().toCharArray();

        for (Character c:expectedPhrase) {
            expected.add(c);
        }

        ArrayList<Character> actual = cryptogram.loadCryptogramFile(TEST_FILE);
        assertEquals(expected, actual);
    }

    /* Should return an array of strings from the test cryptogram file */
    @Test
    void loadCryptograms() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add(EXPECTED_PHRASE);

        assertEquals(expected, cryptogram.loadCryptograms(TEST_FILE));
    }

    /* For each letter in the cryptogram, we should get a mapping back to its actual value */
    @Test
    void getPlainLetter() {

        // Get original characters
        char[] expectedPhrase = EXPECTED_PHRASE.toUpperCase().toCharArray();
        ArrayList<Character> original = new ArrayList<>();
        for (Character c:expectedPhrase) {
            original.add(c);
        }

        ArrayList<String> enciphered = cryptogram.getPhrase();

        for (String s:enciphered) {
            try {
                assertTrue(original.contains(cryptogram.getPlainLetter(s)));
            } catch (CharacterNotFoundException e) {
                fail();
            }
        }


    }

    /*  Ensure our random number generator is working - Should return a number between provided min and max values (Inclusive) */
    @Test
    void getRandomNumber() {
        int MIN = 10; int MAX = 99;
        int RandomNumber = cryptogram.getRandomNumber(MIN, MAX);
        assertTrue((RandomNumber >= MIN) && (RandomNumber <= MAX));
    }

    /* Should string together the entire encrypted phrase */
    @Test
    void displayEncryptedPhrase() {
        assertEquals(15, cryptogram.displayEncryptedPhrase().length());
    }

    /* When passed a phrase, should return a mapping from each distinct character to a distinct enciphered character */
    @Test
    void encipherCryptogram() {
        char[] distinct = "This ate!".toCharArray();

        char[] originalChars = EXPECTED_PHRASE.toCharArray();
        ArrayList<Character> original = new ArrayList<>();
        for (Character c : originalChars) {
            original.add(c);
        }

        HashMap<String, Character> actual = cryptogram.encipherCryptogram(original);

        // Ensure each distinct character from our phrase contains a mapping
        for (Character c : distinct) {
            assertTrue(actual.containsValue(c));
        }
    }

    @Test
    void saveCryptogram() {

        FileService fileService = new FileService("stateful/xxxTestPlayerxxx.txt");
        fileService.deleteFile();

        Player player = new Player("xxxTestPlayerxxx", 100, 4, 4, 1, 1);
        Game game = new Game(player, "letter", "games/cryptograms-test.txt");

        cryptogram.saveCryptogram(player, game);

        cryptogram.loadCryptograms("stateful/xxxTestPlayerxxx.txt");
        assertEquals(6, cryptogram.getNumberEncipheredChars());

        fileService = new FileService("stateful/xxxTestPlayerxxx.txt");
        fileService.deleteFile(); // Cleanup
    }

    @Test
    void getAlphabetAsArray() {
        assertEquals("[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z]", Cryptogram.getAlphabetAsArray().toString());
    }

    @Test
    void arrayToString() {
        ArrayList<String> test = new ArrayList<>();
        test.add("T");
        test.add("e");
        test.add("s");
        test.add("t");

        assertEquals("T|e|s|t|", cryptogram.arrayToString(test));
    }
}