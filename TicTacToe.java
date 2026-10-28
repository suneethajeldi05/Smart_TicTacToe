
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TicTacToe extends JFrame {

    private final JButton[] cells = new JButton[9];
    private final char[] board = new char[9];

    private final Color bg = new Color(20, 24, 43);
    private final Color tile = new Color(39, 46, 73);
    private final Color pink = new Color(255, 100, 155);
    private final Color blue = new Color(75, 195, 255);
    private final Color green = new Color(70, 220, 150);

    private JLabel status;
    private JLabel xScoreLabel, oScoreLabel, drawLabel;
    private JComboBox<String> modeBox, difficultyBox;

    private char currentPlayer = 'X';
    private boolean gameOver = false;

    private int xWins = 0, oWins = 0, draws = 0;

    private final Random random = new Random();

    public TicTacToe() {
        setTitle("Smart Tic-Tac-Toe | AI Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 690);
        setMinimumSize(new Dimension(480, 640));
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));

        JLabel title = new JLabel("SMART TIC-TAC-TOE",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(Color.WHITE);
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 15));
        center.setBackground(bg);

        JPanel settings = new JPanel(new GridLayout(2, 2, 10, 8));
        settings.setBackground(bg);

        JLabel modeLabel = makeLabel("GAME MODE");
        modeBox = new JComboBox<>(
                new String[]{"Two Players", "Play vs Computer"});
        styleCombo(modeBox);

        JLabel difficultyLabel = makeLabel("AI DIFFICULTY");
        difficultyBox = new JComboBox<>(
                new String[]{"Easy", "Medium", "Hard"});
        styleCombo(difficultyBox);

        settings.add(modeLabel);
        settings.add(modeBox);
        settings.add(difficultyLabel);
        settings.add(difficultyBox);

        modeBox.addActionListener(e -> {
            difficultyBox.setEnabled(
                    modeBox.getSelectedIndex() == 1);
            resetBoard();
        });

        difficultyBox.setEnabled(false);

        center.add(settings, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(bg);
        boardPanel.setPreferredSize(new Dimension(420, 390));

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            button.setFont(new Font("Arial", Font.BOLD, 48));
            button.setBackground(tile);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final int index = i;
            button.addActionListener(e -> playMove(index));

            cells[i] = button;
            boardPanel.add(button);
        }

        center.add(boardPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(bg);

        status = makeLabel("Player X's turn");
        status.setFont(new Font("Arial", Font.BOLD, 19));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        status.setForeground(pink);

        bottom.add(status);
        bottom.add(Box.createVerticalStrut(15));

        JPanel scores = new JPanel(new GridLayout(1, 3, 10, 0));
        scores.setBackground(bg);

        xScoreLabel = scoreCard("X WINS", pink, 0);
        oScoreLabel = scoreCard("O WINS", blue, 0);
        drawLabel = scoreCard("DRAWS", Color.YELLOW, 0);

        scores.add(xScoreLabel);
        scores.add(oScoreLabel);
        scores.add(drawLabel);

        bottom.add(scores);
        bottom.add(Box.createVerticalStrut(15));

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setBackground(bg);

        JButton restart = makeButton("RESTART ROUND");
        JButton resetScores = makeButton("RESET SCORES");

        restart.addActionListener(e -> resetBoard());

        resetScores.addActionListener(e -> {
            xWins = 0;
            oWins = 0;
            draws = 0;
            updateScores();
            resetBoard();
        });

        actions.add(restart);
        actions.add(resetScores);

        bottom.add(actions);
        center.add(bottom, BorderLayout.SOUTH);

        main.add(center, BorderLayout.CENTER);
        setContentPane(main);

        resetBoard();
        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(tile);
        combo.setForeground(Color.WHITE);
    }

    private JLabel scoreCard(String title, Color color, int score) {
        JLabel label = new JLabel(
                "<html><center>" + title + "<br><b>"
                        + score + "</b></center></html>",
                SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setOpaque(true);
        label.setBackground(tile);
        label.setBorder(
                BorderFactory.createEmptyBorder(12, 4, 12, 4));
        return label;
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(105, 90, 230));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void playMove(int index) {
        if (gameOver || board[index] != '\0') {
            return;
        }

        board[index] = currentPlayer;
        cells[index].setText(String.valueOf(currentPlayer));
        cells[index].setForeground(
                currentPlayer == 'X' ? pink : blue);

        if (finishIfNeeded()) {
            return;
        }

        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
        updateStatus();

        if (isComputerTurn()) {
            status.setText("Computer is thinking...");
            setBoardEnabled(false);

            // Let the status message render before AI calculation.
            SwingUtilities.invokeLater(() -> {
                computerMove();
                setBoardEnabled(true);
            });
        }
    }

    private boolean isComputerTurn() {
        return modeBox.getSelectedIndex() == 1
                && currentPlayer == 'O'
                && !gameOver;
    }

    private void computerMove() {
        if (gameOver) {
            return;
        }

        int move;
        String difficulty =
                (String) difficultyBox.getSelectedItem();

        if ("Easy".equals(difficulty)) {
            move = randomMove();
        } else if ("Medium".equals(difficulty)) {
            move = findWinningMove('O');

            if (move == -1) {
                move = findWinningMove('X');
            }

            if (move == -1) {
                move = randomMove();
            }
        } else {
            move = bestMove();
        }

        if (move >= 0) {
            board[move] = 'O';
            cells[move].setText("O");
            cells[move].setForeground(blue);

            if (!finishIfNeeded()) {
                currentPlayer = 'X';
                updateStatus();
            }
        }
    }

    private int randomMove() {
        int[] empty = new int[9];
        int count = 0;

        for (int i = 0; i < 9; i++) {
            if (board[i] == '\0') {
                empty[count++] = i;
            }
        }

        return count == 0 ? -1
                : empty[random.nextInt(count)];
    }

    private int findWinningMove(char player) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == '\0') {
                board[i] = player;
                boolean wins = winner() == player;
                board[i] = '\0';

                if (wins) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int bestMove() {
        int bestScore = Integer.MIN_VALUE;
        int move = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == '\0') {
                board[i] = 'O';
                int score = minimax(false, 0);
                board[i] = '\0';

                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }

        return move;
    }

    private int minimax(boolean maximizing, int depth) {
        char result = winner();

        if (result == 'O') return 10 - depth;
        if (result == 'X') return depth - 10;
        if (isFull()) return 0;

        if (maximizing) {
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < 9; i++) {
                if (board[i] == '\0') {
                    board[i] = 'O';
                    best = Math.max(best, minimax(false, depth + 1));
                    board[i] = '\0';
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < 9; i++) {
                if (board[i] == '\0') {
                    board[i] = 'X';
                    best = Math.min(best, minimax(true, depth + 1));
                    board[i] = '\0';
                }
            }
            return best;
        }
    }

    private char winner() {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] line : lines) {
            char a = board[line[0]];

            if (a != '\0'
                    && a == board[line[1]]
                    && a == board[line[2]]) {
                return a;
            }
        }

        return '\0';
    }

    private boolean finishIfNeeded() {
        char result = winner();

        if (result != '\0') {
            gameOver = true;

            for (int i = 0; i < 9; i++) {
                if (board[i] == result) {
                    // Highlight all cells with the winning symbol
                    cells[i].setBackground(
                            new Color(48, 100, 90));
                }
            }

            if (result == 'X') {
                xWins++;
            } else {
                oWins++;
            }

            updateScores();
            status.setText("Player " + result + " wins!");
            status.setForeground(green);
            setBoardEnabled(false);
            return true;
        }

        if (isFull()) {
            gameOver = true;
            draws++;
            updateScores();
            status.setText("It's a draw!");
            status.setForeground(Color.YELLOW);
            setBoardEnabled(false);
            return true;
        }

        return false;
    }

    private boolean isFull() {
        for (char cell : board) {
            if (cell == '\0') {
                return false;
            }
        }
        return true;
    }

    private void updateScores() {
        xScoreLabel.setText("<html><center>X WINS<br><b>"
                + xWins + "</b></center></html>");
        oScoreLabel.setText("<html><center>O WINS<br><b>"
                + oWins + "</b></center></html>");
        drawLabel.setText("<html><center>DRAWS<br><b>"
                + draws + "</b></center></html>");
    }

    private void updateStatus() {
        status.setText("Player " + currentPlayer + "'s turn");
        status.setForeground(currentPlayer == 'X' ? pink : blue);
    }

    private void setBoardEnabled(boolean enabled) {
        for (JButton cell : cells) {
            cell.setEnabled(enabled);
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = '\0';
            cells[i].setText("");
            cells[i].setBackground(tile);
            cells[i].setForeground(Color.WHITE);
            cells[i].setEnabled(true);
        }

        currentPlayer = 'X';
        gameOver = false;
        updateStatus();

        // If the mode changes, the next round starts with X.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToe::new);
    }
}
