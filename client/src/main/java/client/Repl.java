package client;

import java.util.Scanner;

public class Repl {
    private final ServerFacade server = new ServerFacade("http://localhost:8080");
    private String authToken = null;
    private boolean loggedIn = false;

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to Chess!");
        System.out.println("Type 'help' to get started.");

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help":
                    printPreloginHelp();
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
        }
        else {
            switch (input) {
                case "help":
                    printPreloginHelp();
                    break;
                case "logout":
                    logout();
                    break;
                case "create":
                    System.out.println("Create not implemented yet.");
                    break;
                case "list":
                    System.out.println("List not implemented yet.");
                    break;
                case "play":
                    System.out.println("Play not implemented yet.");
                    break;
                case "observe":
                    System.out.println("Observe not implemented yet.");
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default;
                    System.out.println("Unknown command. Type 'help' for options.")
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  help     - show commands");
        System.out.println("  login    - log in");
        System.out.println("  register - create account");
        System.out.println("  quit     - exit");
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

    private void printPreloginHelp() {
        System.out.println("Commands: ");
        System.out.println("  help     - show commands");
        System.out.println("  login    - log in");
        System.out.println("  register - create account");
        System.out.println("  quit     - exit");
    }

    private void printPostloginHelp() {
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
}
