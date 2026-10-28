package lab2.analyzer;

import lab1.analyzer.ExAnalyzer;
import lab1.model.AnalysisResult;
import lab2.model.Sum;
import lab2.model.Tree;
import lab2.parser.Node;
import lab2.parser.Parser;
import lab2.transform.DivisionByZero;
import lab2.transform.Flattener;
import lab2.transform.Simplifier;
import lab2.tree.TreeBuilder;

import java.util.List;

public class ParallelAnalyzer {

    private final ExAnalyzer lab1 = new ExAnalyzer();
    public ParallelResult analyze(String expression) {
        AnalysisResult checked = lab1.analyze(expression);
        if (!checked.isValid()) {
            return new ParallelResult(checked, null, null, null, null, null);
        }
        Node ast = new Parser(checked.tokens()).parse();
        Sum normalized = new Flattener(expression).flatten(ast);
        Tree rawTree = TreeBuilder.buildSum(normalized);

        Simplifier simplifier = new Simplifier();
        Sum simplified = simplifier.simplify(normalized);
        List<DivisionByZero> errors = simplifier.errors();
        if (!errors.isEmpty()) {
            return new ParallelResult(checked, normalized, rawTree, errors, null, null);
        }
        return new ParallelResult(checked, normalized, rawTree, errors, simplified, TreeBuilder.buildSum(simplified));
    }
}
