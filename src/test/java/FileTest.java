import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileTest {

    /* Step by step on the creation of a new player and ensuring it can be found in the players.txt file */

    @Test
    void newPlayerSavedInFile(){
        Player player = new Player("PLAYER_ID", 0, 0, 0, 0, 0);
        Players players = new Players();
        players.addPlayer(player);
        ArrayList<Player> allPlayers = players.getAllPlayers();
        FileService fileService = new FileService("stateful/players.txt");
        fileService.savePlayers(allPlayers);
        ArrayList<String> playersString = fileService.read();
        boolean isFound = false;

        for (String playerString : playersString) {
            String[] tokens = playerString.split(Game.ESCAPE_DELIMITER);
            if (tokens[0].equals(player.getUsername())){
                isFound = true;
            }
        }
        assertTrue(isFound);
    }


}
