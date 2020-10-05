import javax.swing.*;
import java.awt.*;

class GUILogin extends GUI {

    private Players players;

    private JButton loginBtn, newAccountBtn;
    private JButton quit;
    private JComboBox<String> usernameDropdown;
    private JTextField usernameTxt;

    GUILogin() {
        this.players = new Players();
        this.login();
    }

    private void login() {
        close(loginFrame);

        loginFrame = new JFrame("Cryptograms | Login");
        Container contentPane = loginFrame.getContentPane();


        // Let user login or create new account
        JLabel usernameLabelLogin = new JLabel("Login As: ", SwingConstants.RIGHT);
        Players players = new Players();
        usernameDropdown = new JComboBox<>(players.getAllUsernames());
        usernameDropdown.setSelectedIndex(0);

        loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {new GUIGame(players.isPlayer((String) usernameDropdown.getSelectedItem())); close(loginFrame);});

        JLabel usernameLabelNew = new JLabel("New Account: ", SwingConstants.RIGHT);
        usernameTxt = new JTextField(20);

        newAccountBtn = new JButton("Create");
        newAccountBtn.addActionListener(e -> {if (usernameTxt.getText().length() != 0) {new GUIGame(players.isPlayer(usernameTxt.getText())); close(loginFrame);}});

        // Existing account
        JPanel existingAccount = new JPanel(new GridLayout(1, 3));
        existingAccount.add(usernameLabelLogin);
        existingAccount.add(usernameDropdown);
        existingAccount.add(loginBtn);

        // New Account
        JPanel newAccount = new JPanel(new GridLayout(1, 3));
        newAccount.add(usernameLabelNew);
        newAccount.add(usernameTxt);
        newAccount.add(newAccountBtn);


        JPanel actionButtons = new JPanel(new GridLayout(1, 2));

        // Display top 10
        JButton top10Btn = new JButton("Display Scoreboard");
        top10Btn.addActionListener(e -> this.top10Players());

        // Quit
        quit = new JButton("Quit");
        quit.addActionListener(e -> {close(loginFrame); System.exit(0);} );

        actionButtons.add(top10Btn);
        actionButtons.add(quit);

        // Build Layout
        JPanel loginPnl = new JPanel(new GridLayout(3, 1));
        loginPnl.add(existingAccount);
        loginPnl.add(newAccount);
        loginPnl.add(actionButtons);

        contentPane.add(loginPnl, BorderLayout.NORTH);
        contentPane.add(actionButtons, BorderLayout.SOUTH);

        drawFrame(loginFrame, 200, 200);
    }

    private void top10Players() {
        top10Frame = new JFrame("Scoreboard");
        Container contentPane = top10Frame.getContentPane();

        // Build Layout
        JPanel top10Pnl = new JPanel(new GridLayout(12, 1));

        JPanel row = new JPanel(new GridLayout(1, 3));
        row.add(new JLabel("Position", SwingConstants.CENTER));
        row.add(new JLabel("Username", SwingConstants.CENTER));
        row.add(new JLabel("Score", SwingConstants.CENTER));
        top10Pnl.add(row);

        int index = 1;

        for (Player player:players.top10Scores()) {
            row = new JPanel(new GridLayout(1, 3));
            row.add(new JLabel(String.valueOf(index), SwingConstants.CENTER));
            row.add(new JLabel(player.getUsername(), SwingConstants.CENTER));
            row.add(new JLabel(player.getAccuracy(), SwingConstants.CENTER));
            top10Pnl.add(row);
            index++;
        }

        while (index < 11) {
            top10Pnl.add(new JPanel());
            index++;
        }

        top10Pnl.add(closeBtn(top10Frame, "Close", false));

        contentPane.add(top10Pnl);
        drawFrame(top10Frame, 900, 200);
    }
}
