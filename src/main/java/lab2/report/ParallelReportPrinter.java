package lab2.report;

import lab1.report.ColorFormat;
import lab1.report.ReportPrinter;
import lab2.analyzer.ParallelResult;
import lab2.model.Tree;
import lab2.transform.DivisionByZero;

import java.util.Arrays;
import java.util.List;

public class ParallelReportPrinter {

    private final ReportPrinter lab1Printer = new ReportPrinter();

    public void print(ParallelResult r) {
        if (r.hasSyntaxErrors()) {
            lab1Printer.print(r.lab1());
            return;
        }

        String expr = r.lab1().expression();
        System.out.println("Вираз: " + expr);

        System.out.println();
        System.out.println(ColorFormat.CYAN + "До оптимізації:" + ColorFormat.RESET);
        printTree(r.rawTree());

        if (r.hasDivisionErrors()) {
            System.out.println();
            System.out.println(ColorFormat.RED + "Є ділення на 0" + ColorFormat.RESET);
            System.out.println(expr);
            System.out.println(marks(expr, r.divisionErrors()));
            for (DivisionByZero e : r.divisionErrors()) {
                System.out.println(e);
            }
            return;
        }

        System.out.println();
        System.out.println(ColorFormat.GREEN + "Після оптимізації:" + ColorFormat.RESET);
        printTree(r.tree());
    }

    private void printTree(Tree t) {
        for (String line : TreePrinter.drawLevels(t)) {
            System.out.println(line);
        }
        int[] widths = TreePrinter.widthPerLevel(t);
        int ops = Arrays.stream(widths).sum();
        System.out.println("Вираз:  " + TreePrinter.toExpression(t));
        System.out.println("Висота: " + t.height() + ", операцій: " + ops
                + ", операцій на ярусах: " + Arrays.toString(widths));
    }

    private static String marks(String expr, List<DivisionByZero> errors) {
        char[] line = new char[expr.length()];
        Arrays.fill(line, ' ');
        for (DivisionByZero e : errors) {
            if (e.position() >= 0 && e.position() < line.length) {
                line[e.position()] = '^';
            }
        }
        return new String(line).stripTrailing();
    }
}
