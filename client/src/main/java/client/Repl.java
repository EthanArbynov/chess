package client;

import java.util.Scanner;

public class Repl {
    private final ServerFacade server = new ServerFacade("http://localhost:8080");
    private String authToken = null;

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
                    printHelp();
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
            System.out.println("Registered successfully.");
        } catch (Exception e) {
            System.out.println("Register failed: " + e.getMessage());
        }
    }
}
