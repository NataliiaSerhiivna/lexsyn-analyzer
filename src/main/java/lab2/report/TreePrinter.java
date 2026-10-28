package lab2.report;

import lab2.model.Bin;
import lab2.model.Call;
import lab2.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TreePrinter {

    private static final int GAP_SIBLINGS = 1;
    private static final int GAP_GROUPS = 3;

    private TreePrinter() {
    }

    //вираз
    public static String toExpression(Tree t) {
        if (t instanceof Bin b) {
            return wrap(b.left()) + b.op() + wrap(b.right());
        }
        return leafText(t);
    }

    private static String wrap(Tree t) {
        return t instanceof Bin ? "(" + toExpression(t) + ")" : toExpression(t);
    }

    private static String leafText(Tree t) {
        if (t instanceof Call c) {
            List<String> args = new ArrayList<>();
            for (Tree a : c.args()) {
                args.add(toExpression(a));
            }
            return c.name() + "(" + String.join(", ", args) + ")";
        }
        return t.toString();
    }

    //статистика
    public static int[] widthPerLevel(Tree t) {
        int[] widths = new int[t.height()];
        countOps(t, widths);
        return widths;
    }

    private static void countOps(Tree t, int[] widths) {
        if (t instanceof Bin b) {
            widths[b.height() - 1]++;
            countOps(b.left(), widths);
            countOps(b.right(), widths);
        }
    }

    //рис
    public static List<String> drawLevels(Tree t) {
        int rows = t.height() + 1;
        List<char[]> grid = new ArrayList<>();
        int width = layoutWidth(t);
        for (int i = 0; i < rows; i++) {
            char[] line = new char[width];
            Arrays.fill(line, ' ');
            grid.add(line);
        }
        place(t, new int[]{0}, grid);

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            String label = i == 0 ? "листя " : String.format("ярус %d", i);
            lines.add(label + " │ " + new String(grid.get(i)).stripTrailing());
        }
        return lines;
    }

    private static int place(Tree t, int[] cursor, List<char[]> grid) {
        if (t instanceof Bin b) {
            int left = place(b.left(), cursor, grid);
            cursor[0] += b.height() == 1 ? GAP_SIBLINGS : GAP_GROUPS;
            int right = place(b.right(), cursor, grid);
            int center = (left + right) / 2;
            grid.get(b.height())[center] = b.op();
            return center;
        }
        String text = leafText(t);
        int start = cursor[0];
        text.getChars(0, text.length(), grid.get(0), start);
        cursor[0] += text.length();
        return start + (text.length() - 1) / 2;
    }

    private static int layoutWidth(Tree t) {
        if (t instanceof Bin b) {
            int gap = b.height() == 1 ? GAP_SIBLINGS : GAP_GROUPS;
            return layoutWidth(b.left()) + gap + layoutWidth(b.right());
        }
        return leafText(t).length();
    }
}
