import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileServiceTest {

    private static final String filepath = "src/test/resources/";

    @Test
    public void readLinesFromFileThatExists() {
        FileService fileService = new FileService(filepath, "ReadEachLine.txt");
        ArrayList<String> lines = fileService.read();

        assertEquals(4, lines.size());

        assertEquals("Line 0", lines.get(0));
        assertEquals("Line 1", lines.get(1));
        assertEquals("Line 2", lines.get(2));
        assertEquals("Line 3", lines.get(3));
    }

    @Test
    public void readLinesFromFileThatDoesntExist() {
        FileService fileService = new FileService(filepath, "ReadEachLineFromThisFileWhichDoesntExist.txt");
        ArrayList<String> lines = fileService.read();
        assertNull(lines);
    }

    @Test
    public void saveToFileThatExists() {
        FileService fileService = new FileService(filepath, "save/KeepMeHere.txt");

        ArrayList<String> expected = new ArrayList<>();

        // Fill array with 10 random numbers, cast to strings
        for (int i=0; i<10; i++) {
            expected.add(String.valueOf(Math.random()));
        }

        fileService.save(expected);

        // Read from file
        ArrayList<String> actual = fileService.read();

        assertEquals(10, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void saveToFileThatDoesntExist() {
        String filename = "save/deleteMeFirst.txt";

        // Delete file if exists
        File file = new File(filepath + filename);
        //noinspection ResultOfMethodCallIgnored
        file.delete();

        // Create new file
        FileService fileService = new FileService(filepath, filename);

        ArrayList<String> expected = new ArrayList<>();

        // Fill array with 10 random numbers, cast to strings
        for (int i=0; i<10; i++) {
            expected.add(String.valueOf(Math.random()));
        }

        fileService.save(expected);

        // Read from file
        ArrayList<String> actual = fileService.read();

        assertEquals(10, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void savePlayersToFile() {
        Player player1 = new Player("Player 1", 0, 0, 0, 0, 0);
        Player player2 = new Player("Player 2", 100, 4, 4, 1, 1);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        FileService fileService = new FileService(filepath, "players/saved.txt");
        fileService.savePlayers(players);
    }
}