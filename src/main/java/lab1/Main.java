package lab1;

import lab1.analyzer.ExAnalyzer;
import lab1.model.AnalysisResult;
import lab1.report.ReportPrinter;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ExAnalyzer analyzer = new ExAnalyzer();
        ReportPrinter printer = new ReportPrinter();

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Введіть вираз і натисніть Enter для завершення введіть exit ");
            System.out.println();

            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine();
                String expression = line.strip();

                if (expression.isEmpty() || expression.equalsIgnoreCase("exit")
                        || expression.equalsIgnoreCase("quit")) {
                    break;
                }

                AnalysisResult result = analyzer.analyze(expression);
                printer.print(result);
                System.out.println();
            }
        }

        System.out.println("Роботу завершено");
    }
}