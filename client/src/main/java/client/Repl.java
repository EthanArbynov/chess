package client;

import chess.*;
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

            boolean blackPerspective = color.equals("BLACK");
            WSClient ws = new WSClient(authToken, game.gameID(), blackPerspective);
            ws.connect();

            System.out.println("Joined game successfully.");
            runGameplayLoop(scanner, ws, false);

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

            WSClient ws = new WSClient(authToken, game.gameID(), false);
            ws.connect();

            System.out.println("Observing game: " + game.gameName());
            runGameplayLoop(scanner, ws, true);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Observe failed: " + e.getMessage());
        }
    }

    private void runGameplayLoop(Scanner scanner, WSClient ws, boolean observing) {
        boolean inGame = true;

        printGameplayHelp(observing);

        while (inGame) {
            System.out.print("[game] > ");
            String input = scanner.nextLine().trim().toLowerCase();

            try {
                switch (input) {
                    case "help":
                        printGameplayHelp(observing);
                        break;
                    case "redraw":
                        System.out.println("Board redraw comes from the latest LOAD_GAME message.");
                        break;
                    case "move":
                        if (observing) {
                            System.out.println("Observers cannot make moves.");
                        } else {
                            makeMove(scanner, ws);
                        }
                        break;
                    case "leave":
                        ws.sendLeave();
                        ws.close();
                        inGame = false;
                        System.out.println("Left game.");
                        break;
                    case "resign":
                        if (observing) {
                            System.out.println("Observers cannot resign.");
                            break;
                        }

                        System.out.print("Are you sure you want to resign? (yes/no): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();

                        if (!confirm.equals("yes")) {
                            break;
                        }

                        ws.sendResign();
                        System.out.println("You resigned.");
                        break;
                    case "highlight":
                        highlightMoves(scanner, ws);
                        break;
                    default:
                        System.out.println("Unknown command. Type 'help' for options.");
                }
            } catch (Exception e) {
                System.out.println("Gameplay error: " + e.getMessage());
            }
        }
    }

    private void printGameplayHelp(boolean observing) {
        System.out.println("Gameplay commands:");
        System.out.println("  help      - show commands");
        System.out.println("  redraw    - redraw board");
        System.out.println("  leave     - leave the game");
        System.out.println("  highlight - highlight legal moves");

        if (!observing) {
            System.out.println("  move      - make a move");
            System.out.println("  resign    - resign the game");
        }
    }

    private void makeMove(Scanner scanner, WSClient ws) throws Exception {
        System.out.print("Start position (example e2): ");
        String startText = scanner.nextLine().trim().toLowerCase();

        System.out.print("End position (example e4): ");
        String endText = scanner.nextLine().trim().toLowerCase();

        ChessPosition start = parsePosition(startText);
        ChessPosition end = parsePosition(endText);

        System.out.print("Promotion piece? (QUEEN/ROOK/BISHOP/KNIGHT or blank): ");
        String promoText = scanner.nextLine().trim().toUpperCase();

        ChessPiece.PieceType promotion = null;
        if (!promoText.isEmpty()) {
            promotion = ChessPiece.PieceType.valueOf(promoText);
        }

        ChessMove move = new ChessMove(start, end, promotion);
        ws.sendMove(move);
    }

    private ChessPosition parsePosition(String text) {
        if (text.length() != 2) {
            throw new IllegalArgumentException("Bad position.");
        }

        char file = text.charAt(0);
        char rank = text.charAt(1);

        int col = file - 'a' + 1;
        int row = rank - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Bad position.");
        }

        return new ChessPosition(row, col);
    }

    private void highlightMoves(Scanner scanner, WSClient ws) {
        try {
            ChessGame game = ws.getCurrentGame();
            if (game == null) {
                System.out.println("No game loaded yet.");
                return;
            }

            System.out.print("Piece position (example e2): ");
            String posText = scanner.nextLine().trim().toLowerCase();

            ChessPosition position = parsePosition(posText);

            if (game.getBoard().getPiece(position) == null) {
                System.out.println("No piece at that position.");
                return;
            }

            var legalMoves = game.validMoves(position);
            if (legalMoves == null) {
                System.out.println("No legal moves.");
                return;
            }

            BoardPrinter.drawBoardWithHighlights(game.getBoard(), blackPerspective(ws), position, legalMoves);

        } catch (Exception e) {
            System.out.println("Highlight failed: " + e.getMessage());
        }
    }

    private boolean blackPerspective(WSClient ws) {
        return ws.isBlackPerspective();
    }
}
