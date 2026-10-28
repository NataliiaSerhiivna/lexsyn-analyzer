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

        Item result = mergeLowest(items, plusOp, minusOp);
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
        Tree inside = mergeLowest(withNeutral, plusOp, minusOp).tree();
        return inside.height() < onTop.height() ? inside : onTop;
    }
   private static Item mergeLowest(List<Item> items, char plusOp, char minusOp) {
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
