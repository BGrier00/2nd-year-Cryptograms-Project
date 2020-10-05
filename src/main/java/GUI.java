import javax.swing.*;
import java.awt.*;

public class GUI {
    JFrame resumeGameFrame, currentGameStateFrame, makeGuessFrame, gameWonFrame, gameForfeitFrame, playerStats, loginFrame, top10Frame;

    void close(Frame frame) {
        if (frame != null) {
            frame.dispose();
        }
    }

    void drawFrame(JFrame frame, int x, int y) {
        frame.pack();
        frame.setLocation(x, y);
        frame.setVisible(true);
    }

    JButton closeBtn(JFrame frame, String label, boolean startNewGame) {
        JButton closeBtn = new JButton(label);
        closeBtn.addActionListener(e -> {
            close(frame);

            if (startNewGame) {
                new GUILogin(); // Begin a new game
            }
        });

        return closeBtn;
    }

}
