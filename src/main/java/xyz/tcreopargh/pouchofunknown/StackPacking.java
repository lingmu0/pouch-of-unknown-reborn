package xyz.tcreopargh.pouchofunknown;

import java.util.List;

/** Pure packing logic: only decoded compatible stacks may be combined. */
final class StackPacking {
    private StackPacking() {}

    interface Adapter<E, S> {
        S decode(E entry);
        E encode(S stack);
        S copy(S stack);
        boolean empty(S stack);
        boolean matches(S left, S right);
        int count(S stack);
        int max(S stack);
        void count(S stack, int count);
    }

    static <E, S> S insert(List<E> entries, S input, int capacity, Adapter<E, S> adapter) {
        S remaining = adapter.copy(input);
        for (int i = 0; i < entries.size() && !adapter.empty(remaining); i++) {
            S stored = adapter.decode(entries.get(i));
            if (adapter.empty(stored) || !adapter.matches(stored, remaining)) continue;
            int amount = Math.min(adapter.count(remaining),
                    Math.max(0, adapter.max(stored) - adapter.count(stored)));
            if (amount == 0) continue;
            adapter.count(stored, adapter.count(stored) + amount);
            adapter.count(remaining, adapter.count(remaining) - amount);
            entries.set(i, adapter.encode(stored));
        }
        while (!adapter.empty(remaining) && entries.size() < capacity) {
            int amount = Math.min(adapter.count(remaining), adapter.max(remaining));
            S part = adapter.copy(remaining);
            adapter.count(part, amount);
            adapter.count(remaining, adapter.count(remaining) - amount);
            entries.add(adapter.encode(part));
        }
        return remaining;
    }
}
