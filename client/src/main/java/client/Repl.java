package client;

import chess.ChessBoard;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Repl {
    private final ServerFacade server = new ServerFacade("http://localhost:8080");
    private String authToken = null;
    private boolean loggedIn = false;
    private List<GameData> lastGames = new ArrayList<>();

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to Chess!");
        System.out.println("Type 'help' to get started.");

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (!loggedIn) {
                switch (input) {
                    case "help":
                        printPreLoginHelp();
                        break;
                    case "login":
                        login(scanner);
                        break;
                    case "register":
                        register(scanner);
                        break;
                    case "quit":
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown command. Type 'help' for options.");
                }
            } else {
                switch (input) {
                    case "help":
                        printPostLoginHelp();
                        break;
                    case "logout":
                        logout();
                        break;
                    case "create":
                        createGame(scanner);
                        break;
                    case "list":
                        listGames();
                        break;
                    case "play":
                        playGame(scanner);
                        break;
                    case "observe":
                        observeGame(scanner);
                        break;
                    case "quit":
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown command. Type 'help' for options.");
                }
            }
        }
    }

    private void login(Scanner scanner) {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            authToken = server.login(username, password);
            loggedIn = true;
            System.out.println("Logged in successfully.");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void register(Scanner scanner) {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            authToken = server.register(username, password, email);
            loggedIn = true;
            System.out.println("Registered successfully.");
        } catch (Exception e) {
            System.out.println("Register failed: " + e.getMessage());
        }
    }

    private void printPreLoginHelp() {
        System.out.println("Commands: ");
        System.out.println("  help     - show commands");
        System.out.println("  login    - log in");
        System.out.println("  register - create account");
        System.out.println("  quit     - exit");
    }

    private void printPostLoginHelp() {
        System.out.println("Commands:");
        System.out.println("  help     - show commands");
        System.out.println("  logout   - log out");
        System.out.println("  create   - create a game");
        System.out.println("  list     - list games");
        System.out.println("  play     - join a game");
        System.out.println("  observe  - observe a game");
        System.out.println("  quit     - exit");
    }

    private void logout() {
        try {
            server.logout(authToken);
            authToken = null;
            loggedIn = false;
            System.out.println("Logged out successfully.");
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void createGame(Scanner scanner) {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine();

            server.createGame(authToken, gameName);
            System.out.println("Game created successfully.");
        } catch (Exception e) {
            System.out.println("Create failed: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            lastGames = server.listGames(authToken);

            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games found.");
                return;
            }

            for (int i = 0; i < lastGames.size(); i++) {
                GameData game = lastGames.get(i);

                String white = game.whiteUsername() == null ? "(open)" : game.whiteUsername();
                String black = game.blackUsername() == null ? "(open)" : game.blackUsername();

                System.out.println((i + 1) + ". " + game.gameName()
                        + " | White: " + white
                        + " | Black: " + black);
            }
        } catch (Exception e) {
            System.out.println("List failed: " + e.getMessage());
        }
    }

    private void playGame(Scanner scanner) {
        try {
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games available. Use 'list' first.");
                return;
            }

            System.out.print("Game number: ");
            String numberInput = scanner.nextLine().trim();
            int number = Integer.parseInt(numberInput);

            if (number < 1 || number > lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            System.out.print("Color (WHITE or BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color.");
                return;
            }

            GameData game = lastGames.get(number - 1);
            server.joinGame(authToken, color, game.gameID());

            System.out.println("Joined game successfully.");

            ChessBoard board = new ChessBoard();
            board.resetBoard();

            boolean blackPerspective = color.equals("BLACK");
            BoardPrinter.drawBoard(board, blackPerspective);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Play failed: " + e.getMessage());
        }
    }

    private void observeGame(Scanner scanner) {
        try {
            if (lastGames == null || lastGames.isEmpty()) {
                System.out.println("No games available. Use 'list' first.");
                return;
            }

            System.out.print("Game number: ");
            String numberInput = scanner.nextLine().trim();
            int number = Integer.parseInt(numberInput);

            if (number < 1 || number > lastGames.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameData game = lastGames.get(number - 1);

            System.out.println("Observing game: " + game.gameName());

            ChessBoard board = new ChessBoard();
            board.resetBoard();
            BoardPrinter.drawBoard(board, false);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Observe failed: " + e.getMessage());
        }
    }
}
