package xyz.tcreopargh.pouchofunknown;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A pouch owns its contents. Unreadable entries are kept intact for later recovery. */
public final class PouchStorage {
    private static final String CONTENTS = "Inventory";
    private PouchStorage() {}

    private static ListTag read(ItemStack pouch) {
        CompoundTag root = pouch.getTag();
        return root == null ? new ListTag() : root.getList(CONTENTS, Tag.TAG_COMPOUND).copy();
    }

    private static void write(ItemStack pouch, ListTag contents) {
        pouch.getOrCreateTag().put(CONTENTS, contents);
    }

    private static ItemStack decode(CompoundTag tag, Level level) { return ItemStack.of(tag); }
    private static CompoundTag encode(ItemStack stack, Level level) { return stack.save(new CompoundTag()); }
    private static boolean matches(ItemStack a, ItemStack b) { return ItemStack.isSameItemSameTags(a, b); }

    public static int size(ItemStack pouch) { return read(pouch).size(); }

    /** Returns the portion that did not fit; never mutates the incoming stack. */
    public static ItemStack insert(ItemStack pouch, ItemStack incoming, Level level) {
        if (incoming.isEmpty() || incoming.getItem() == PouchOfUnknown.POUCH.get()) return incoming.copy();
        ListTag raw = read(pouch);
        List<CompoundTag> entries = new ArrayList<>(raw.size());
        for (int i = 0; i < raw.size(); i++) entries.add(raw.getCompound(i).copy());
        StackPacking.Adapter<CompoundTag, ItemStack> adapter = new StackPacking.Adapter<>() {
            public ItemStack decode(CompoundTag entry) { return PouchStorage.decode(entry, level); }
            public CompoundTag encode(ItemStack stack) { return PouchStorage.encode(stack, level); }
            public ItemStack copy(ItemStack stack) { return stack.copy(); }
            public boolean empty(ItemStack stack) { return stack.isEmpty(); }
            public boolean matches(ItemStack left, ItemStack right) { return PouchStorage.matches(left, right); }
            public int count(ItemStack stack) { return stack.getCount(); }
            public int max(ItemStack stack) { return stack.getMaxStackSize(); }
            public void count(ItemStack stack, int count) { stack.setCount(count); }
        };
        ItemStack remainder = StackPacking.insert(entries, incoming, PouchOfUnknown.CAPACITY.get(), adapter);
        if (remainder.getCount() != incoming.getCount()) {
            ListTag result = new ListTag();
            for (CompoundTag entry : entries) result.add(entry);
            write(pouch, result);
        }
        return remainder;
    }

    public static int retrieve(ItemStack pouch, net.minecraft.server.level.ServerPlayer player) {
        ListTag contents = read(pouch);
        int retrieved = 0;
        boolean changed = false;
        for (int i = contents.size() - 1; i >= 0; i--) {
            ItemStack stored = decode(contents.getCompound(i), player.level());
            if (stored.isEmpty() || PouchEvents.isLocked(player, stored)) continue;
            int before = stored.getCount();
            player.getInventory().add(stored);
            if (!stored.isEmpty() && player.isShiftKeyDown() && player.drop(stored.copy(), false) != null) {
                stored = ItemStack.EMPTY;
            }
            if (stored.getCount() != before) {
                retrieved++;
                changed = true;
                if (stored.isEmpty()) contents.remove(i);
                else contents.set(i, encode(stored, player.level()));
            }
        }
        if (changed) {
            write(pouch, contents);
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        }
        return retrieved;
    }
}
