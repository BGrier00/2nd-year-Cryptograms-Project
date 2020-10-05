import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Players {

    private String filepath = "src/main/resources/";
    private String filename = "stateful/players.txt";
    private ArrayList<Player> allPlayers;

    /**
     * Load all players from file
     */
    Players() {
        loadPlayers();
    }

    Players(String filepath, String filename) {
        this.filepath = filepath;
        this.filename = filename;
        loadPlayers();
    }

    ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    void addPlayer(Player p) {
        allPlayers.add(p);
    }

    /**
     * Provided a username, return the player that matches that username. If player does not exist, create a new player
     *
     * @param username Username to search
     * @return Player object
     */
    Player isPlayer(String username) {

        FileService fileService = new FileService(filename);
        for (Player player : allPlayers) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }

        // Player doesn't exist
        Player newPlayer = new Player(username, 0, 0, 0, 0, 0);
        allPlayers.add(newPlayer);
        fileService.savePlayers(allPlayers);
        return newPlayer;
    }

    private void loadPlayers() {
        FileService fileService = new FileService(filepath, filename);
        ArrayList<String> players = fileService.read();
        allPlayers = new ArrayList<>();

        for (String playerString : players) {
            String[] tokens = playerString.split(Game.ESCAPE_DELIMITER);

            if (tokens.length != 6) {
                System.out.println("Failed to load: " + playerString + "\n" +
                        "As it could not be correctly parsed. Please Check the players file for errors\n" +
                        "Expected 6 attributes, but found " + tokens.length + "\n");

                continue;
            }

            this.allPlayers.add(
                    new Player(
                            tokens[0], // Username
                            Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]),
                            Integer.parseInt(tokens[3]),
                            Integer.parseInt(tokens[4]),
                            Integer.parseInt(tokens[5])
                    )
            );
        }

        Collections.sort(allPlayers);

        fileService.savePlayers(allPlayers);
    }

    void updatePlayer(Player p) {
        for (Player player : allPlayers) {
            if (p.getUsername().equals(player.getUsername())) {
                allPlayers.remove(player);
                allPlayers.add(p);
                break;
            }
        }

        FileService fileService = new FileService(filename);
        fileService.savePlayers(allPlayers);
    }

    String[] getAllUsernames() {
        ArrayList<String> usernames = new ArrayList<>();

        for (Player player : allPlayers) {
            usernames.add(player.getUsername());
        }

        return usernames.toArray(new String[0]);
    }

    /**
     * @return arraylist of top 10 players based on accuracy
     */
    ArrayList<Player> top10Scores() {
        ArrayList<Player> top10 = new ArrayList<>();

        allPlayers.sort(Comparator.comparing(Player::getAccuracyNumeric).reversed());

        int topNum;
        if (allPlayers.size() >= 10) {
            topNum = 10;

        } else {
            topNum = allPlayers.size();
        }

        for (int i = 0; i < topNum; i++) {
            top10.add(allPlayers.get(i));
        }

        return top10;
    }
}