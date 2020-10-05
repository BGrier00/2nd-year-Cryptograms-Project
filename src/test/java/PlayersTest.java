import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

class PlayersTest {

    private static final String filepath = "src/test/resources/";
    private static final String filename = "players/testPlayers.txt";

    private Players players;
    private Player player;


    @BeforeEach
    void setUp() {
        players = new Players(filepath, filename);
    }

    @Test
    void isPlayerThatDoesExist() {
        assertEquals("Player 1", players.isPlayer("Player 1").getUsername());
        assertEquals(10, players.isPlayer("Player 1").getTotalGuesses());
    }

    @Test
    void isPlayerThatDoesNotExist() {
        assertEquals("New Player", players.isPlayer("New Player").getUsername());
        assertEquals(0, players.isPlayer("New Player").getTotalGuesses());
    }

    @Test
    void getAllPlayers() {
        assertEquals(2, players.getAllPlayers().size());
    }


    @Test
    void addPlayer() {
        player = new Player("Test Player", 50, 20, 10, 1, 1);
        players.addPlayer(player);
        assertEquals(player,players.isPlayer("Test Player") );
        assertEquals("50.00%", players.isPlayer("Test Player").getAccuracy());
    }

    @Test
    void updatePlayer() {
        Player player = players.isPlayer("Player 2");

        player.incrementTotalGuesses();
        player.incrementCryptogramsPlayed();
        player.incrementCorrectGuesses();
        player.incrementCryptogramsCompleted();

        players.updatePlayer(player);

        Assertions.assertEquals(player, players.isPlayer("Player 2"));
    }

    @Test
    void getAllUsernames() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Player 1");
        expected.add("Player 2");

        Assertions.assertEquals(expected.toString(), Arrays.toString(players.getAllUsernames()));
    }

    @Test
    void top10Scores() {
        ArrayList<Player> expected = new ArrayList<>();
        expected.add(new Player("Player 2", 100, 4, 4, 1, 1));
        expected.add(new Player("Player 1", 10, 10,0,0,0));

        assertEquals(expected.toString(), players.top10Scores().toString());
    }


}