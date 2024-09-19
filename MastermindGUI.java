import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class MastermindGUI extends JFrame {
    private final Color[] colorOptions = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK};
    private final JButton[] guessButtons = new JButton[4];
    private final JPanel[] slotRows = new JPanel[10];
    private final JLabel[] feedbackLabels = new JLabel[10];
    private final Color[] secretCode = new Color[4];
    private final Color[][] previousGuesses = new Color[10][4]; // Store previous guesses
    private final Random rand = new Random();
    private int guessCount = 0; // To track the number of guesses

    public MastermindGUI() {
        initializeGame();
    }

    private void initializeGame() {
        setTitle("Mastermind Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        generateSecretCode();
        JPanel guessPanel = createGuessPanel();
        JPanel slotsPanel = createSlotsPanel();
        JButton submitButton = createSubmitButton();

        // Initialize feedback labels as empty
        for (int i = 0; i < feedbackLabels.length; i++) {
            feedbackLabels[i] = new JLabel(""); // Set initially empty
            slotRows[i].add(feedbackLabels[i]); // Add feedback label to each row
        }

        add(guessPanel, BorderLayout.NORTH);
        add(slotsPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        pack();
        setSize(400, 600); // Fixed size for the board
        setVisible(true);
    }

    private JPanel createGuessPanel() {
        JPanel panel = new JPanel();
        for (int i = 0; i < guessButtons.length; i++) {
            JButton button = new JButton();
            button.setBackground(Color.LIGHT_GRAY);
            button.addActionListener(this::handleColorSelection);
            guessButtons[i] = button;
            panel.add(button);
        }
        return panel;
    }

    private JPanel createSlotsPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 1));
        for (int i = 0; i < slotRows.length; i++) {
            slotRows[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
            for (int j = 0; j < 4; j++) {
                JButton guessSlot = new JButton();
                guessSlot.setBackground(Color.LIGHT_GRAY);
                guessSlot.setEnabled(false); // Disable slots for past guesses
                slotRows[i].add(guessSlot);
            }
            panel.add(slotRows[i]);
        }
        return panel;
    }

    private JButton createSubmitButton() {
        JButton submitButton = new JButton("Submit Guess");
        submitButton.addActionListener((ActionEvent e) -> {
            if (guessCount < 10) {
                Color[] guess = new Color[4];
                for (int i = 0; i < 4; i++) {
                    guess[i] = guessButtons[i].getBackground();
                    previousGuesses[guessCount][i] = guess[i];
                }
                String feedback = getFeedback(guess);
                feedbackLabels[guessCount].setText(feedback); // Update the feedback label
                updateGuessDisplay(guessCount, guess); // Update the GUI to show the guess
                guessCount++;

                // Check if the game is over
                if (feedback.contains("Correct: 4")) {
                    JOptionPane.showMessageDialog(this, "Congratulations! You've guessed the correct code!");
                    replayGame();
                } else if (guessCount == 10) {
                    showGameOverMessage();
                }
            }
        });
        return submitButton;
    }

    private void generateSecretCode() {
        for (int i = 0; i < secretCode.length; i++) {
            secretCode[i] = colorOptions[rand.nextInt(colorOptions.length)];
        }
        // Debugging: Print the secret code to the console
        System.out.print("Secret Code: ");
        for (Color color : secretCode) {
            System.out.print(getColorName(color) + " ");
        }
        System.out.println();
    }

    private void handleColorSelection(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        Color currentColor = source.getBackground();
        int nextColorIndex = (getColorIndex(currentColor) + 1) % colorOptions.length;
        source.setBackground(colorOptions[nextColorIndex]);
    }

    private int getColorIndex(Color color) {
        for (int i = 0; i < colorOptions.length; i++) {
            if (colorOptions[i].equals(color)) {
                return i;
            }
        }
        return -1; // Default case, should not occur
    }

    private String getFeedback(Color[] guess) {
        int correctColorAndPosition = 0;
        int correctColorOnly = 0;
        boolean[] secretUsed = new boolean[4];
        boolean[] guessUsed = new boolean[4];

        // First pass: Check for correct color and position
        for (int i = 0; i < guess.length; i++) {
            if (guess[i].equals(secretCode[i])) {
                correctColorAndPosition++;
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Second pass: Check for correct color only
        for (int i = 0; i < guess.length; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < secretCode.length; j++) {
                    if (!secretUsed[j] && guess[i].equals(secretCode[j])) {
                        correctColorOnly++;
                        secretUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return "Correct: " + correctColorAndPosition + ", Color Only: " + correctColorOnly;
    }

    private void updateGuessDisplay(int rowIndex, Color[] guess) {
        JPanel rowPanel = slotRows[rowIndex];
        Component[] components = rowPanel.getComponents();
        for (int i = 0; i < guess.length; i++) {
            if (components[i] instanceof JButton) {
                JButton guessSlot = (JButton) components[i];
                guessSlot.setBackground(guess[i]);
            }
        }
    }

    private void showGameOverMessage() {
        // Show the correct answer with colors
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Game Over! The correct code was:"));
        for (Color color : secretCode) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setEnabled(false);
            messagePanel.add(colorButton);
        }
        JOptionPane.showMessageDialog(this, messagePanel, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        replayGame();
    }

    private void replayGame() {
        int option = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Replay", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            new MastermindGUI();
        } else {
            System.exit(0);
        }
    }

    private String getColorName(Color color) {
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.GREEN)) return "Green";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.YELLOW)) return "Yellow";
        if (color.equals(Color.ORANGE)) return "Orange";
        if (color.equals(Color.PINK)) return "Pink";
        return "Unknown";
    }

    public static void main(String[] args) {
        new MastermindGUI();
    }
}
