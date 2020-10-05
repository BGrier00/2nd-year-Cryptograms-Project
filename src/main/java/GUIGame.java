import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.min;

public class GUIGame extends GUI implements ActionListener {
    private Player player;
    private Game game;

    private final static Integer CHARACTERS_PER_LINE = 25;

    JButton resumeGameBtn, newGameBtn, playBtn;
    private JRadioButton datasetTest, datasetDemo, cryptogramLetter, cryptogramNumber;
    private JFrame hintHelp;

    public GUIGame(Player player) {
        this.player = player;
        this.resumeGame();
    }

    /**
     * If user has a saved game state, ask if they would like to resume from it
     */
    void resumeGame() {
        if (player.hasSavedGame()) {
            resumeGameFrame = new JFrame("Resume Saved Game?");
            Container contentPane = resumeGameFrame.getContentPane();

            resumeGameBtn = new JButton("Resume Saved Game");
            resumeGameBtn.addActionListener(this);

            newGameBtn = new JButton("Start New Game");
            newGameBtn.addActionListener(this);

            // Build Layout
            JPanel resumeGamePnl = new JPanel(new GridLayout(3, 1));
            resumeGamePnl.add(new JLabel("Resume From Saved Game, or Create New Game?"));
            resumeGamePnl.add(resumeGameBtn);
            resumeGamePnl.add(newGameBtn);

            contentPane.add(resumeGamePnl);

            drawFrame(resumeGameFrame, 200, 200);
        } else {
            this.newGame();
        }
    }

    void resumeFromSavedGame() throws FileNotFoundException {
        this.game = new Game(player);
        close(resumeGameFrame);
        this.playing();
    }

    /**
     * Choose the dataset and cryptogram type to be played
     */
    void newGame() {
        resumeGameFrame = new JFrame("Play New Game");
        Container contentPane = resumeGameFrame.getContentPane();


        // DATASET
        JLabel datasetLbl = new JLabel("Choose Dataset: ", SwingConstants.RIGHT);

        datasetTest = new JRadioButton("Test");
        datasetDemo = new JRadioButton("Demo");

        ButtonGroup dataset = new ButtonGroup();
        dataset.add(datasetTest);
        dataset.add(datasetDemo);


        // CRYPTOGRAM TYPE
        JLabel cryprogramTypeLbl = new JLabel("Cryptogram Type: ", SwingConstants.RIGHT);

        cryptogramLetter = new JRadioButton("Letter");
        cryptogramNumber = new JRadioButton("Number");

        ButtonGroup cryptogramType = new ButtonGroup();
        cryptogramType.add(cryptogramLetter);
        cryptogramType.add(cryptogramNumber);


        // PLAY BUTTON
        playBtn = new JButton("Play");
        playBtn.addActionListener(this);

        // Build Layout
        JPanel newGamePnl = new JPanel(new GridLayout(3, 3));
        newGamePnl.add(datasetLbl);
        newGamePnl.add(datasetTest);
        newGamePnl.add(datasetDemo);

        newGamePnl.add(cryprogramTypeLbl);
        newGamePnl.add(cryptogramLetter);
        newGamePnl.add(cryptogramNumber);

        newGamePnl.add(new JLabel());
        newGamePnl.add(playBtn);

        contentPane.add(newGamePnl);

        drawFrame(resumeGameFrame, 200, 200);
    }


    void playing() {
        // Check if player has won
        if (game.hasWon()) {
            gameWonGUI();

        } else {
            currentGameStateGUI();
        }
    }

    private void currentGameStateGUI() {
        close(currentGameStateFrame); // Close frame if already open

        currentGameStateFrame = new JFrame("Cryptograms | Playing");
        Container contentPane = currentGameStateFrame.getContentPane();

        ArrayList<String> encipheredPhrase = game.getEncipheredPhrase();
        HashMap<String, Integer> frequencies = game.getCryptogram().getFrequencies();
        ArrayList<GameGuess> guesses = game.getGuesses();

        int cols = min(encipheredPhrase.size(), CHARACTERS_PER_LINE);
        int rows = encipheredPhrase.size() / cols;

        while (cols * rows < encipheredPhrase.size()) { rows ++; }

        JPanel playingPnl = new JPanel(new GridLayout(rows + 1, 1)); // +1 For action buttons at bottom

        for (int i = 0; i < rows; i++) { // For each row

            JPanel rowPnl = new JPanel(new GridLayout(4, 1));

            JPanel encipheredPnl = new JPanel(new GridLayout(1, cols));
            JPanel frequenciesPnl = new JPanel(new GridLayout(1, cols));
            JPanel guessesPnl = new JPanel(new GridLayout(1, cols));

            for (int j = 0; j < cols; j++) { // For each col in row
                int index = (i * cols) + j;

                if (index >= encipheredPhrase.size()) { // Avoid Out of bounds exception
                    encipheredPnl.add(new JLabel());
                    guessesPnl.add(new JLabel());
                    frequenciesPnl.add(new JLabel());
                    continue;
                }

                encipheredPnl.add(new JLabel(encipheredPhrase.get(index), SwingConstants.CENTER)); // Enciphered Chars

                GameGuess guess = guesses.get(index);
                if (guess.couldHaveBeenEnciphered()) {
                    JButton guessBtn = new JButton(guess.getGuess());
                    guessBtn.addActionListener(e -> this.makeGuessGUI(guess) );

                    guessesPnl.add(guessBtn);

                    // Record letter frequency
                    frequenciesPnl.add(new JLabel(String.valueOf(frequencies.get(guess.getEnciphered())), SwingConstants.CENTER));

                // Record answer
                } else {
                    guessesPnl.add(new JLabel(guess.getGuess(), SwingConstants.CENTER));
                    frequenciesPnl.add(new JLabel());
                }
            }

            // Build Layout
            rowPnl.add(encipheredPnl);
            rowPnl.add(guessesPnl);
            if (game.showLetterFrequency()) {rowPnl.add(frequenciesPnl); }

            playingPnl.add(rowPnl);
        }

        // Build Layout
        playingPnl.add(getGameHelpButtons());
        contentPane.add(playingPnl, BorderLayout.NORTH);

        drawFrame(currentGameStateFrame, 200, 200);
    }

    // Display an alphabet from which to make the guess
    private void makeGuessGUI(GameGuess guess) {
        close(makeGuessFrame);

        makeGuessFrame = new JFrame("Cryptograms | Make Guess");
        Container contentPane = makeGuessFrame.getContentPane();

        // Letters from alphabet
        String alphabet = Cryptogram.ALPHABET;
        JPanel alphabetPnl = new JPanel(new GridLayout(1, alphabet.length() / 9));

        for (Character letter : Cryptogram.getAlphabetAsArray()) {

            JButton letterBtn = new JButton(String.valueOf(letter));
            letterBtn.addActionListener(e -> {
                close(makeGuessFrame);
                game.enterLetter(guess.getEnciphered(), letter);
                playing();
            });

            // Check if this letter has already been used in a guess
            if (game.getPlayerGameMapping().containsValue(letter)) {
                letterBtn.setEnabled(false);
            }

            alphabetPnl.add(letterBtn);
        }

        // Build layout
        JPanel makeGuessPnl = new JPanel(new GridLayout(2, 1));
        makeGuessPnl.add(new JLabel("Guessing for: " + guess.getEnciphered()));
        makeGuessPnl.add(alphabetPnl);
        contentPane.add(makeGuessPnl, BorderLayout.NORTH);

        drawFrame(makeGuessFrame, 200, 400);
    }

    private void gameWonGUI() {
        close(currentGameStateFrame);
        close(gameWonFrame); // Close frame if already open
        close(hintHelp);

        gameWonFrame = new JFrame("Game Won!");
        Container contentPane = gameWonFrame.getContentPane();

        // Build layout
        JPanel gameWonPnl = new JPanel(new GridLayout(3, 1));
        gameWonPnl.add(new JLabel("Game Statistics", SwingConstants.CENTER));
        gameWonPnl.add(game.getGameStatsPnl());
        gameWonPnl.add(closeBtn(gameWonFrame, "Back To Menu", true));
        contentPane.add(gameWonPnl, BorderLayout.NORTH);

        drawFrame(gameWonFrame, 200, 200);

        // Display player stats
        GUIPlayer guiPlayer = new GUIPlayer(player);
        guiPlayer.displayPlayerStats();
    }

    private void gameForfeit() {

        try {
            game.forfeit();
        } catch (CharacterNotFoundException e) {
            close(gameForfeitFrame);
            close(currentGameStateFrame);
        }

        currentGameStateGUI();

        close(gameForfeitFrame); // Close if already open

        gameForfeitFrame = new JFrame("Cryptograms | Forfeit!");
        Container contentPane = gameForfeitFrame.getContentPane();

        JButton closeGameBtn = new JButton("Close");
        closeGameBtn.addActionListener(e -> {
            close(gameForfeitFrame);
            close(currentGameStateFrame);
            new GUILogin(); // Begin a new game
        });

        // Build layout
        JPanel gameWonPnl = new JPanel(new GridLayout(3, 1));
        gameWonPnl.add(new JLabel("You gave up :("));
        gameWonPnl.add(new JLabel("Better Luck Next Time"));
        gameWonPnl.add(closeGameBtn);
        contentPane.add(gameWonPnl, BorderLayout.NORTH);

        drawFrame(gameForfeitFrame, 200, 200);
        currentGameStateFrame.setEnabled(false); // Disable main game window
    }

    private JPanel getGameHelpButtons() {
        JPanel helpBtnsPnl = new JPanel(new GridLayout(1, 7));

        JButton undoBtn = new JButton("Undo Move");
        undoBtn.addActionListener(e -> {
            close(currentGameStateFrame);
            game.undo();
            currentGameStateGUI();
        });
        undoBtn.setEnabled(game.canUndo());
        helpBtnsPnl.add(undoBtn);

        JButton freqBtn = new JButton("Letter Frequency: " + game.showLetterFrequencyString());
        freqBtn.addActionListener(e -> {
            close(currentGameStateFrame);
            game.toggleShowLetterFrequency();
            currentGameStateGUI();
        });
        helpBtnsPnl.add(freqBtn);

        JButton answerBtn = new JButton("Show Solution (forfeit)");
        answerBtn.addActionListener(e -> {
            close(currentGameStateFrame);
            gameForfeit();
        });
        helpBtnsPnl.add(answerBtn);

        JButton playerStatsBtn = new JButton("Player Stats");
        playerStatsBtn.addActionListener(e -> {
            GUIPlayer guiPlayer = new GUIPlayer(player);
            guiPlayer.displayPlayerStats();
        });
        helpBtnsPnl.add(playerStatsBtn);

        JButton saveGameBtn = new JButton("Save");
        saveGameBtn.addActionListener(e -> {
            close(currentGameStateFrame);
            game.saveGame();
            new GUILogin(); // Save Game
        });
        helpBtnsPnl.add(saveGameBtn);

        JButton hintBtn = new JButton("Hint");
        hintBtn.addActionListener(e -> {
            close(currentGameStateFrame);
            try {
                String z = game.getHint();
                if (z == null){
                    hintHelpGUI();                 // Cannot understand why this does not work.
                }
            } catch (CharacterNotFoundException ex) {
                ex.printStackTrace();
            }
            playing();
        });
        helpBtnsPnl.add(hintBtn);


        helpBtnsPnl.add(closeBtn(currentGameStateFrame, "Quit", true));

        return helpBtnsPnl;
    }

    private void hintHelpGUI(){
        close(hintHelp);

        hintHelp = new JFrame("Remove a guess first!");
        Container contentPane = hintHelp.getContentPane();

        JButton closeGameBtn = new JButton("Close");
        closeGameBtn.addActionListener(e -> close(hintHelp));

        JPanel hintHelpPn1 = new JPanel(new GridLayout(2, 1));
        hintHelpPn1.add(new JLabel("Remove a guess to get a hint!"));
        hintHelpPn1.add(closeGameBtn);
        contentPane.add(hintHelpPn1, BorderLayout.NORTH);

        hintHelp.pack();
        hintHelp.setLocation(400, 600);
        hintHelp.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == resumeGameBtn) {
            try {
                this.resumeFromSavedGame();

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error: Could not resume a game; We will now create a new game: " + e.getMessage());
                close(resumeGameFrame);
                this.newGame();
            }
        }

        if (event.getSource() == newGameBtn) {
            close(resumeGameFrame);
            this.newGame();
        }

        if (event.getSource() == playBtn) {
            String dataset = "";
            String cryptogramType = "";

            if (datasetTest.isSelected()) {
                dataset = "games/test.txt";

            } else if (datasetDemo.isSelected()) {
                dataset = "games/cryptograms.txt";
            }

            if (cryptogramLetter.isSelected()) {
                cryptogramType = "letter";
            } else if (cryptogramNumber.isSelected()) {
                cryptogramType = "number";
            }

            if (!dataset.equals("") && !cryptogramType.equals("")) {
                close(resumeGameFrame);

                this.game = new Game(this.player, cryptogramType, dataset);
                this.playing();
            }
        }

    }
}
