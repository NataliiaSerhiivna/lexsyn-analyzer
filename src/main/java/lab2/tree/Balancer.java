package lab2.tree;

import lab2.model.Bin;
import lab2.model.Const;
import lab2.model.Fraction;
import lab2.model.Sign;
import lab2.model.Tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Balancer {
    private record Item(Sign sign, Tree tree, int order) {
    }
    private static final Comparator<Item> BY_HEIGHT_THEN_ORDER =
            Comparator.<Item>comparingInt(it -> it.tree().height()).thenComparingInt(Item::order);

    private Balancer() {
    }
    public static Tree balance(List<Sign> signs, List<Tree> trees, char plusOp, char minusOp, Fraction neutral) {
        if (trees.isEmpty()) {
            return Const.of(neutral);
        }
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++) {
            items.add(new Item(signs.get(i), trees.get(i), i));
        }

        Item result = best(items, plusOp, minusOp);
        if (result.sign() == Sign.PLUS) {
            return result.tree();
        }
        if (plusOp == '+' && result.tree() instanceof Const c) {
            return Const.of(c.value().negate());
        }
        Tree onTop = new Bin(minusOp, Const.of(neutral), result.tree());
        List<Item> withNeutral = new ArrayList<>();
        withNeutral.add(new Item(Sign.PLUS, Const.of(neutral), -1));
        withNeutral.addAll(items);
        Tree inside = best(withNeutral, plusOp, minusOp).tree();
        return inside.height() < onTop.height() ? inside : onTop;
    }

    private static Item best(List<Item> items, char plusOp, char minusOp) {
        Item ordered = balanceInOrder(items, plusOp, minusOp);
        Item reordered = balanceWithReorder(items, plusOp, minusOp);
        return ordered.tree().height() <= reordered.tree().height() ? ordered : reordered;
    }
    private static Item balanceInOrder(List<Item> items, char plusOp, char minusOp) {
        int n = items.size();
        int[][] height = new int[n][n];
        int[][] levels = new int[n][n];
        int[][] leaves = new int[n][n];
        int[][] split = new int[n][n];
        for (int i = 0; i < n; i++) {
            height[i][i] = items.get(i).tree().height();
            levels[i][i] = sumOfLevels(items.get(i).tree());
            leaves[i][i] = countLeaves(items.get(i).tree());
        }
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i + len - 1 < n; i++) {
                int j = i + len - 1;
                int bestHeight = Integer.MAX_VALUE;
                int bestLevels = Integer.MAX_VALUE;
                int bestBalance = Integer.MAX_VALUE;
                for (int k = j - 1; k >= i; k--) {
                    int h = Math.max(height[i][k], height[k + 1][j]) + 1;
                    int lv = levels[i][k] + levels[k + 1][j] + h;
                    int balance = Math.abs(leaves[i][k] - leaves[k + 1][j]);
                    boolean better = h < bestHeight
                            || (h == bestHeight && lv < bestLevels)
                            || (h == bestHeight && lv == bestLevels && balance < bestBalance);
                    if (better) {
                        bestHeight = h;
                        bestLevels = lv;
                        bestBalance = balance;
                        split[i][j] = k;
                    }
                }
                height[i][j] = bestHeight;
                levels[i][j] = bestLevels;
                leaves[i][j] = leaves[i][i] + leaves[i + 1][j];
            }
        }
        return build(items, split, 0, n - 1, plusOp, minusOp);
    }
    private static int sumOfLevels(Tree t) {
        if (t instanceof Bin b) {
            return b.height() + sumOfLevels(b.left()) + sumOfLevels(b.right());
        }
        return 0;
    }
    private static Item build(List<Item> items, int[][] split, int i, int j, char plusOp, char minusOp) {
        if (i == j) {
            return items.get(i);
        }
        int k = split[i][j];
        Item left = build(items, split, i, k, plusOp, minusOp);
        Item right = build(items, split, k + 1, j, plusOp, minusOp);
        return merge(left, right, plusOp, minusOp);
    }

    private static int countLeaves(Tree t) {
        if (t instanceof Bin b) {
            return countLeaves(b.left()) + countLeaves(b.right());
        }
        return 1;
    }
    private static Item balanceWithReorder(List<Item> items, char plusOp, char minusOp) {
        PriorityQueue<Item> heap = new PriorityQueue<>(BY_HEIGHT_THEN_ORDER);
        heap.addAll(items);
        while (heap.size() > 1) {
            Item a = heap.poll();
            Item b = heap.poll();
            boolean aFirst = a.order() < b.order();
            heap.add(merge(aFirst ? a : b, aFirst ? b : a, plusOp, minusOp));
        }
        return heap.poll();
    }
    private static Item merge(Item x, Item y, char plusOp, char minusOp) {
        if (x.sign() == Sign.PLUS && y.sign() == Sign.PLUS) {
            return new Item(Sign.PLUS, new Bin(plusOp, x.tree(), y.tree()), x.order());
        }
        if (x.sign() == Sign.PLUS) {
            return new Item(Sign.PLUS, new Bin(minusOp, x.tree(), y.tree()), x.order());
        }
        if (y.sign() == Sign.PLUS) {
            return new Item(Sign.PLUS, new Bin(minusOp, y.tree(), x.tree()), x.order());
        }
        return new Item(Sign.MINUS, new Bin(plusOp, x.tree(), y.tree()), x.order());
    }
}
