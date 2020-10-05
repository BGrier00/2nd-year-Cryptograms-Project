import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class FileService {

    private String filepath = "src/main/resources/";
    private final String filename;

    FileService(String filename) {
        this.filename = filename;
    }

    FileService(String filepath, String filename) {
        this.filepath = filepath;
        this.filename = filename;
    }

    /**
     * Load all lines from given file into an array list
     * NOTE: If opening a users' saved cryptogram, then there will only be one item in the ArrayList
     *
     * @return ArrayList of cryptograms from this file
     */
    ArrayList<String> read() {
        ArrayList<String> lines = new ArrayList<>();

        File file = new File(filepath + filename);

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine())
                lines.add(sc.nextLine());

            sc.close();

        } catch (IOException e) {
            System.out.println("Unable to locate file. Please ensure this file exists: " + filepath + filename);
            return null;
        }

        return lines;
    }

    /**
     * Save an array of strings to the specified file
     *
     * @param output Array of strings to be saved, 1 index per line
     * @return If the file was created successfully
     */
    boolean save(ArrayList<String> output) {

        File file = new File(this.filepath + this.filename);

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();// Ensure all parent directories are made

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false); // Override file if exists, otherwise create new
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            for (String s : output) {
                bw.write(s);
                bw.newLine();
            }

            bw.close();

            return true;

        } catch (IOException e) {
            System.out.println("An error occurred whilst trying to save the file: " + e);
        }

        return false;
    }

    /**
     * Save all players to file
     *
     * @param players List of players to be saved
     * @return True if saved successfully
     */
    boolean savePlayers(ArrayList<Player> players) {
        File file = new File(this.filepath + this.filename);

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();// Ensure all parent directories are made

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false); // Override file if exists, otherwise create new
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            for (Player player : players) {
                bw.write(player.toString());
                bw.newLine();
            }

            bw.close();
            return true;

        } catch (IOException e) {
            System.out.println("An error occurred whilst trying to save the file: " + e);
        }

        return false;
    }

    void deleteFile() {
        File file = new File(this.filepath + this.filename);
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    boolean exists() {
        return new File(this.filepath + this.filename).exists();
    }
}