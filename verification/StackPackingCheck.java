package xyz.tcreopargh.pouchofunknown;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Runs without a game launch: javac StackPacking.java StackPackingCheck.java; java ...StackPackingCheck. */
public final class StackPackingCheck {
    private static final class Stack {
        final String item;
        final int limit;
        int count;
        Stack(String item, int count, int limit) { this.item = item; this.count = count; this.limit = limit; }
    }
    private static final StackPacking.Adapter<Stack, Stack> ADAPTER = new StackPacking.Adapter<>() {
        public Stack decode(Stack entry) { return entry.item.equals("missing") ? new Stack("", 0, 64) : copy(entry); }
        public Stack encode(Stack stack) { return copy(stack); }
        public Stack copy(Stack stack) { return new Stack(stack.item, stack.count, stack.limit); }
        public boolean empty(Stack stack) { return stack.count == 0; }
        public boolean matches(Stack a, Stack b) { return a.item.equals(b.item); }
        public int count(Stack stack) { return stack.count; }
        public int max(Stack stack) { return stack.limit; }
        public void count(Stack stack, int count) { stack.count = count; }
    };
    private static void require(boolean condition) {
        if (!condition) throw new AssertionError("Stack packing lost or duplicated items");
    }
    public static void main(String[] args) {
        List<Stack> entries = new ArrayList<>(List.of(new Stack("a", 54, 64), new Stack("a", 60, 64)));
        Stack input = new Stack("a", 50, 64);
        Stack rest = StackPacking.insert(entries, input, 3, ADAPTER);
        require(input.count == 50 && rest.count == 0 && entries.size() == 3);
        require(entries.get(0).count == 64 && entries.get(1).count == 64 && entries.get(2).count == 36);

        entries = new ArrayList<>(List.of(new Stack("a", 60, 64)));
        rest = StackPacking.insert(entries, input, 1, ADAPTER);
        require(entries.get(0).count == 64 && rest.count == 46);

        entries = new ArrayList<>(List.of(new Stack("missing", 7, 64), new Stack("other", 10, 64)));
        rest = StackPacking.insert(entries, new Stack("a", 70, 64), 3, ADAPTER);
        require(entries.get(0).item.equals("missing") && entries.get(0).count == 7);
        require(entries.get(1).count == 10 && entries.get(2).count == 64 && rest.count == 6);

        entries = new ArrayList<>();
        rest = StackPacking.insert(entries, new Stack("tool", 2, 1), 2, ADAPTER);
        require(rest.count == 0 && entries.size() == 2 && entries.get(0).count == 1 && entries.get(1).count == 1);
        Random random = new Random(1423);
        for (int trial = 0; trial < 10000; trial++) {
            entries = new ArrayList<>();
            int originalA = 0, originalOther = 0;
            for (int i = 0, n = random.nextInt(8); i < n; i++) {
                String id = random.nextBoolean() ? "a" : "other";
                int count = 1 + random.nextInt(64);
                entries.add(new Stack(id, count, 64));
                if (id.equals("a")) originalA += count; else originalOther += count;
            }
            int oldSize = entries.size();
            int capacity = 1 + random.nextInt(8);
            input = new Stack("a", 1 + random.nextInt(200), 64);
            int incoming = input.count;
            rest = StackPacking.insert(entries, input, capacity, ADAPTER);
            int finalA = 0, finalOther = 0;
            for (Stack item : entries) {
                require(item.count > 0 && item.count <= item.limit);
                if (item.item.equals("a")) finalA += item.count; else finalOther += item.count;
            }
            require(input.count == incoming && originalA + incoming == finalA + rest.count);
            require(originalOther == finalOther && entries.size() <= Math.max(oldSize, capacity));
        }
        System.out.println("Stack packing checks passed");
    }
}
