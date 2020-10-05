import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    private Player player;

    @Test
    public void getUsername() {
        player = new Player("Test Player", 0,0,0,0,0);
        assertEquals("Test Player", player.getUsername());
    }

    @Test
    public void getAccuracy() {
        player = new Player("Test Player", 0, 0, 0, 0, 0);
        assertEquals("0%", player.getAccuracy());

        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertEquals("50.00%", player.getAccuracy());

        player = new Player("Test Player", 33, 100, 33, 1, 1);
        assertEquals("33.00%", player.getAccuracy());
    }

    @Test
    public void getCorrectGuesses() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertEquals(10, player.getCorrectGuesses());
    }

    @Test
    public void getTotalGuesses() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertEquals(20, player.getTotalGuesses());
    }

    @Test
    public void setTotalGuesses() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        player.setTotalGuesses(21);
        assertEquals(21, player.getTotalGuesses());
    }

    @Test
    public void getCryptogramsPlayed() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertEquals(1, player.getCryptogramsPlayed());
    }

    @Test
    public void setCryptogramsPlayed() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        player.setCryptogramsPlayed(2);
        assertEquals(2, player.getCryptogramsPlayed());
    }

    @Test
    public void getCryptogramsCompleted() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertEquals(1, player.getCryptogramsCompleted());
    }

    @Test
    public void setCryptogramsCompleted() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        player.setCryptogramsCompleted(2);
        assertEquals(2, player.getCryptogramsCompleted());
    }

    @Test
    public void hasSavedGame() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        assertFalse(player.hasSavedGame());
    }

    @Test
    public void testToString() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);

        String expected = "Test Player|50|20|10|1|1|";

        assertEquals(expected, player.toString());
    }
}