package lab1.report;

import lab1.model.AnalysisError;
import lab1.model.AnalysisResult;
import lab1.model.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ReportPrinter {

    public void print(AnalysisResult result) {
        System.out.println("Вираз: " + result.expression());

        if (result.isValid()) {
            System.out.println(ColorFormat.GREEN + result.expression() + ColorFormat.RESET);
            System.out.println("Токени: " + formatTokens(result.tokens()));
            return;
        }

        System.out.println(ColorFormat.RED + "Аналізатор виявив помилку(-ки)!" + ColorFormat.RESET);
        System.out.println(buildMarkedLine(result.expression(), result.errors()));

        for (AnalysisError error : result.errors()) {
            System.out.println("Позиція " + error.position() + ": " + error.message());
        }
    }

    private String formatTokens(List<Token> tokens) {
        return tokens.stream().map(Token::toString).collect(Collectors.joining(" "));
    }

    private String buildMarkedLine(String expr, List<AnalysisError> errors) {
        int maxPos = errors.stream().mapToInt(AnalysisError::position).max().orElse(-1);
        String display = expr;
        if (maxPos >= expr.length()) {
            display = expr + "?";
        }

        char[] marks = new char[display.length()];
        java.util.Arrays.fill(marks, ' ');
        for (AnalysisError error : errors) {
            if (error.position() < marks.length) {
                marks[error.position()] = '^';
            }
        }

        return display + "\n" + new String(marks);
    }
}
