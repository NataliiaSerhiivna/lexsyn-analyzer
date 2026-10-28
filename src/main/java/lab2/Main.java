package lab2;

import lab2.analyzer.ParallelAnalyzer;
import lab2.report.ParallelReportPrinter;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ParallelAnalyzer analyzer = new ParallelAnalyzer();
        ParallelReportPrinter printer = new ParallelReportPrinter();

        if (args.length > 0) {
            for (String arg : args) {
                printer.print(analyzer.analyze(arg.strip()));
                System.out.println();
            }
            return;
        }

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Введіть вираз і натисніть Enter, для завершення введіть exit");
            System.out.println();

            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String expression = scanner.nextLine().strip();
                if (expression.isEmpty() || expression.equalsIgnoreCase("exit")
                        || expression.equalsIgnoreCase("quit")) {
                    break;
                }

                printer.print(analyzer.analyze(expression));
                System.out.println();
            }
        }

        System.out.println("Роботу завершено");
    }
}
