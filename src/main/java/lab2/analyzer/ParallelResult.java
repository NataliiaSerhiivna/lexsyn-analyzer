package lab2.analyzer;

import lab1.model.AnalysisResult;
import lab2.model.Sum;
import lab2.model.Tree;
import lab2.transform.DivisionByZero;

import java.util.List;

public record ParallelResult(
        AnalysisResult lab1,
        Sum normalized,
        Tree rawTree,
        List<DivisionByZero> divisionErrors,
        Sum simplified,
        Tree tree
) {

    public boolean hasSyntaxErrors() {
        return !lab1.isValid();
    }

    public boolean hasDivisionErrors() {
        return divisionErrors != null && !divisionErrors.isEmpty();
    }
}
