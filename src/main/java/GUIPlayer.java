import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIPlayer extends GUI {

    Player player;

    GUIPlayer(Player player) {
        this.player = player;
    }

    void displayPlayerStats() {
        playerStats = new JFrame("All-Time Player Statistics");
        Container contentPane = playerStats.getContentPane();

        // Build Layout
        JPanel statsPnl = new JPanel(new GridLayout(3, 1));
        statsPnl.add(new JLabel("All-Time Player Statistics", SwingConstants.CENTER));
        statsPnl.add(player.getPlayerStatsPnl());
        statsPnl.add(closeBtn(playerStats, "Close", false));

        contentPane.add(statsPnl);

        drawFrame(playerStats, 500, 200);
    }

}
