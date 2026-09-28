import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

/** BIT256/BSC211 Assignment 1 - Question 1. Uses a LinkedList to store teams. */
public class JavaProgrammingCompetition extends JFrame {
    private static final String NAME = "CHILESHE MULENGA";
    private static final String STUDENT_NUMBER = "BIT24233394";
    private static final int MAX_TEAMS = 6, TOTAL_ROUNDS = 4;

    private final LinkedList<Team> teams = new LinkedList<Team>();
    private final JTextArea output = new JTextArea();
    private int completedRounds;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JavaProgrammingCompetition().setVisible(true);
            }
        });
    }
    public JavaProgrammingCompetition() {
        setTitle("JAVA PROGRAMMING COMPETITION - " + NAME + "_" + STUDENT_NUMBER);
        setSize(820, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("JAVA PROGRAMMING COMPETITION", JLabel.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(14, 10, 4, 10));
        add(heading, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 12, 12, 12));

        addButton(panel, "Register Team", new Runnable() {
            public void run() {
                registerTeam();
            }
        });
        addButton(panel, "Display Teams", new Runnable() {
            public void run() {
                displayTeams();
            }
        });
        addButton(panel, "Delete Team", new Runnable() {
            public void run() {
                deleteTeam();
            }
        });
        addButton(panel, "Capture Scores", new Runnable() {
            public void run() {
                captureScores();
            }
        });
        addButton(panel, "Find Winner", new Runnable() {
            public void run() {
                findWinner();
            }
        });
        addButton(panel, "Search Team", new Runnable() {
            public void run() {
                searchTeam();
            }
        });
        addButton(panel, "Clear", new Runnable() {
            public void run() {
                output.setText("");
            }
        });
        addButton(panel, "Exit", new Runnable() {
            public void run() {
                dispose();
            }
        });

        add(panel, BorderLayout.SOUTH);
        showText("Welcome. Register all six teams before entering scores.");
    }

    private void addButton(JPanel panel, String label, final Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void registerTeam() {
        if (teams.size() == MAX_TEAMS) {
            error("Only six teams may be registered.");
            return;
        }

        if (completedRounds > 0) {
            error("Registration is closed.");
            return;
        }

        Integer number = positive("Enter a unique team number:");
        if (number == null) {
            return;
        }

        if (find(number) != null) {
            error("Team number already exists.");
            return;
        }

        String university = text("Enter sponsoring university:");
        String one = text("Enter Student 1 name:");
        String two = text("Enter Student 2 name:");
        String three = text("Enter Student 3 name:");

        if (university == null || one == null || two == null || three == null) {
            return;
        }

        teams.add(new Team(number, university, one, two, three));
        showText("Team " + number + " registered successfully.");
    }

    private void displayTeams() {
        if (teams.isEmpty()) {
            showText("No teams have been registered.");
            return;
        }

        StringBuilder result = new StringBuilder("ALL REGISTERED TEAMS\n\n");

        for (Team team : teams) {
            result.append(team).append("\n");
        }

        showText(result.toString());
    }

    private void deleteTeam() {
        if (completedRounds > 0) {
            error("Teams cannot be deleted after scoring begins.");
            return;
        }

        Integer number = positive("Enter team number to delete:");
        if (number == null) {
            return;
        }

        Team team = find(number);

        if (team == null) {
            error("Team not found.");
            return;
        }

        teams.remove(team);
        showText("Team " + number + " was deleted.");
    }

    private void captureScores() {
        if (teams.size() != MAX_TEAMS) {
            error("Register exactly six teams first.");
            return;
        }

        if (completedRounds == TOTAL_ROUNDS) {
            error("All four rounds are complete.");
            return;
        }

        int round = completedRounds + 1;

        for (Team team : teams) {
            if (team.active) {
                Integer score = score("Round " + round + " score for Team "
                        + team.number + " (" + team.university + "):");

                if (score == null) {
                    showText("Round cancelled. No scores were saved.");
                    return;
                }

                team.scores[completedRounds] = score;
            }
        }

        for (Team team : teams) {
            if (team.active) {
                team.total += team.scores[completedRounds];
            }
        }

        Team eliminated = lowest(completedRounds);
        eliminated.active = false;
        eliminated.eliminatedRound = round;
        completedRounds++;

        showText("Round " + round + " complete. Eliminated: Team "
                + eliminated.number + " - " + eliminated.university + ".");
    }

    private void findWinner() {
        if (completedRounds < TOTAL_ROUNDS) {
            error("Complete all four rounds first.");
            return;
        }

        Team winner = null;

        for (Team team : teams) {
            if (team.active && (winner == null
                    || team.total > winner.total
                    || (team.total == winner.total && team.number < winner.number))) {
                winner = team;
            }
        }

        showText("WINNER\n\n" + winner);
    }

    private void searchTeam() {
        Integer number = positive("Enter team number to search:");

        if (number == null) {
            return;
        }

        Team team = find(number);

        if (team == null) {
            showText("Team not found.");
        } else {
            showText("SEARCH RESULT\n\n" + team);
        }
    }

    private Team lowest(int scoreIndex) {
        Team lowest = null;

        for (Team team : teams) {
            if (team.active && (lowest == null
                    || team.scores[scoreIndex] < lowest.scores[scoreIndex]
                    || (team.scores[scoreIndex] == lowest.scores[scoreIndex]
                    && team.total < lowest.total)
                    || (team.scores[scoreIndex] == lowest.scores[scoreIndex]
                    && team.total == lowest.total
                    && team.number < lowest.number))) {
                lowest = team;
            }
        }

        return lowest;
    }

    private Team find(int number) {
        for (Team team : teams) {
            if (team.number == number) {
                return team;
            }
        }

        return null;
    }

    private String text(String question) {
        String value = JOptionPane.showInputDialog(this, question);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private Integer positive(String question) {
        try {
            int value = Integer.parseInt(text(question));
            return value > 0 ? value : null;
        } catch (Exception e) {
            error("Enter a whole number greater than zero.");
            return null;
        }
    }

    private Integer score(String question) {
        String answer = text(question + " (0 to 100)");

        if (answer == null) {
            return null;
        }

        try {
            int value = Integer.parseInt(answer);

            if (value >= 0 && value <= 100) {
                return value;
            }
        } catch (NumberFormatException e) {
        }

        error("Score must be a whole number from 0 to 100.");
        return null;
    }

    private void showText(String message) {
        output.setText(message);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class Team {
        int number;
        int total;
        int eliminatedRound;
        String university;
        String[] students;
        int[] scores = new int[TOTAL_ROUNDS];
        boolean active = true;

        Team(int number, String university, String one, String two, String three) {
            this.number = number;
            this.university = university;
            students = new String[]{one, two, three};
        }

        public String toString() {
            return "Team " + number + " | " + university
                    + "\nStudents: " + students[0] + ", "
                    + students[1] + ", " + students[2]
                    + "\nTotal: " + total + " | "
                    + (active ? "Active" : "Eliminated in round " + eliminatedRound)
                    + "\n";
        }
    }
}