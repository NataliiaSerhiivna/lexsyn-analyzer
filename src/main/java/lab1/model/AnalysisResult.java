package lab1.model;

import java.util.List;

public record AnalysisResult(
        String expression,
        List<Token> tokens,
        List<AnalysisError> errors,
        List<BracketPair> bracketPairs,
        boolean isValid
) {
}
