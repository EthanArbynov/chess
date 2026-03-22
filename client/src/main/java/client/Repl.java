package client;

import java.util.Scanner;

public class Repl {
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
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("Login not implemented yet.");
        System.out.println("You entered username: " + username);
    }

    private void register(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("Register not implemented yet.");
        System.out.println("You entered username: " + username + ", email: " + email);
    }
}
