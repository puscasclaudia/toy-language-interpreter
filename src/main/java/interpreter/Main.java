package interpreter;

import interpreter.model.adt.*;
import interpreter.controller.Controller;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import java.util.Scanner;

public class Main {
    private static void runProgram(IStmt program) {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), program);
        Controller ctrl = new Controller(new Repository(state), true);
        try {
            ctrl.allStep();
        } catch (Exception e) {
            System.err.println("Execution Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Toy Interpreter Menu ---");
            System.out.println("1. Run Example 1 (int v; v=2; print(v))");
            System.out.println("2. Run Example 2 (int a; int b; a=2+3*5; b=a+1; print(b))");
            System.out.println("3. Run Example 3 (bool a; int v; a=true; if a then v=2 else v=3; print(v))");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> runProgram(Examples.EX1);
                case "2" -> runProgram(Examples.EX2);
                case "3" -> runProgram(Examples.EX3);
                case "0" -> System.exit(0);
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }
}