import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        Player player = new Player("PLAYER_ID", 0, 0, 0, 0, 0);
        game = new Game(player, "letter", "games/cryptograms-test.txt");
    }

    @Test
    void getHintWin() throws CharacterNotFoundException {
        for(int i = 0;i<6;i++)
        game.getHint();
        assertTrue(game.hasWon());
    }

    @Test
    void getHintWhenWon() throws CharacterNotFoundException {
        for(int i = 0;i<6;i++)
            game.getHint();

        // PLAYER HAS WON AT THIS POINT //

        String shouldBeNull = game.getHint();
        assertNull(shouldBeNull);
    }

    @Test
    void getHintWhenFull() throws CharacterNotFoundException {
        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();

        int charVal = 65;

        for(String character : encipheredPhrase){
            game.enterLetter(character, (char) 65);
            charVal++;
        }

        // Looks something like
        // ABCCD EDFCG   (filled with wrong answers)
        assertNull(game.getHint());
    }

    @Test
    void getHintNoCorrectGuesses() throws CharacterNotFoundException {
        for(int i = 0;i<6;i++)
            game.getHint();

        assertEquals(game.getCorrectGuesses(),0);
    }

    @Test
    void getHintNoIncorrectGuesses() throws CharacterNotFoundException {
        for(int i = 0;i<6;i++)
            game.getHint();

        assertEquals(game.getIncorrectGuesses(),0);
    }

    @Test
    void canUndoHint() {
        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();
        HashMap<String, Character> map = game.getPlayerGameMapping();

        game.enterLetter(encipheredPhrase.get(0), 'T');
        game.undo();
        assertEquals(map.size(),0);
    }

    @Test
    void dupsRemoved() {

        ArrayList<String> array = new ArrayList<>();
        array.add("A");
        array.add("A");
        array.add("B");

        array = game.removeDups(array);
        assertEquals(array.size(), 2);

    }

    @Test
    void numDupsRemoved() {
        ArrayList<String> array = new ArrayList<>();
        array.add("1");
        array.add("1");
        array.add("2");

        array = game.removeDupNumbers(array);
        assertEquals(array.size(), 2);
    }

    @Test
    void isNumerical(){
        ArrayList<String> array = new ArrayList<>();
        array.add("1");
        array.add("2");
        array.add("A");

        assertTrue(Game.isNumeric(array.get(0)));
        assertFalse(Game.isNumeric(array.get(2)));

    }

    @Test
    void getAccuracyWhenHinting() throws CharacterNotFoundException {

        for(int i = 0;i<6;i++)
            game.getHint();

        assertEquals(game.getAccuracy(),"No Guesses");
    }

    @Test
    void canUndoLetterHint() {
        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();
        HashMap<String, Character> map = game.getPlayerGameMapping();

        game.enterLetter(encipheredPhrase.get(0), 'T');
        game.undoLetter(encipheredPhrase.get(0), 'T');
        assertEquals(map.size(),0);
    }

    @Test
    void canUndo(){
        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();
        game.enterLetter(encipheredPhrase.get(0), 'T');

        assertTrue(game.canUndo());
    }

    @Test
    void getHintReplace() throws CharacterNotFoundException {

        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();
        HashMap<String, Character> map = game.getPlayerGameMapping();

        game.enterLetter(encipheredPhrase.get(0), 'T');     // T--- -- - T--T!
        game.enterLetter(encipheredPhrase.get(1), 'E');     // TE-- -- - T--T!
        game.enterLetter(encipheredPhrase.get(2), 'I');     // TEI- I- - T--T!
        game.enterLetter(encipheredPhrase.get(3), 'S');     // TEIS IS - T-ST!
        game.enterLetter(encipheredPhrase.get(8), 'A');     // TEIS IS A T-ST!
        game.getHint();                                            // T-IS IS A TEST!

        assertNull(map.get(encipheredPhrase.get(1)));              // make sure Array index 1 is null

        game.enterLetter(encipheredPhrase.get(1), 'H');     // THIS IS A TEST!

        assertEquals('H', map.get(encipheredPhrase.get(1)));
        assertEquals('E', map.get(encipheredPhrase.get(11)));
        assertTrue(game.hasWon());
    }


    @Test
    void generateCryptogram() throws IllegalAccessException {
        assertEquals(6, game.generateCryptogram("letter").getNumberEncipheredChars());
    }

    @Test
    void isUndoLetter() {
        game.addPlayerGameMapping("i", 'g');
        assertTrue(game.isUndoLetter("i", 'g')); // Remove it

        assertFalse(game.isUndoLetter("i", 'g'));
    }

    @Test
    void hasGuessBeenUsed() {
        game.addPlayerGameMapping("i", 'g');
        assertTrue(game.hasGuessBeenUsed('g'));
    }

    @Test
    void showLetterFrequencyString() {
        Assertions.assertFalse(game.showLetterFrequency());
        Assertions.assertEquals("Off", game.showLetterFrequencyString());

        game.toggleShowLetterFrequency();

        Assertions.assertTrue(game.showLetterFrequency());
        Assertions.assertEquals("On", game.showLetterFrequencyString());
    }


}