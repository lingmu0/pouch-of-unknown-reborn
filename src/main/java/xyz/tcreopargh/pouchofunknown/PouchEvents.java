package xyz.tcreopargh.pouchofunknown;

import com.alessandro.astages.api.holder.AHolder;
import com.alessandro.astages.engine.ARestrictionManager;
import com.alessandro.astages.engine.store.Attributes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;

public final class PouchEvents {
    private static boolean eligible(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() != PouchOfUnknown.POUCH.get();
    }

    static boolean inventoryLocked(ServerPlayer player, ItemStack stack) {
        return eligible(stack) && ARestrictionManager.ITEM_INSTANCE
                .getInventoryRestriction(AHolder.serverAndPlayer(player), stack) != null;
    }

    static boolean equipmentLocked(ServerPlayer player, ItemStack stack) {
        return eligible(stack) && ARestrictionManager.ITEM_INSTANCE
                .getEquipmentRestriction(AHolder.serverAndPlayer(player), stack) != null;
    }

    static boolean isLocked(ServerPlayer player, ItemStack stack) {
        if (!eligible(stack)) return false;
        if (inventoryLocked(player, stack)) return true;
        var restriction = ARestrictionManager.ITEM_INSTANCE.getRestriction(AHolder.serverAndPlayer(player), stack);
        return restriction != null && restriction.isDisabled(Attributes.PICKUP);
    }

    private static ItemStack findPouch(ServerPlayer player) {
        if (ModList.get().isLoaded("curios")) {
            ItemStack equipped = CuriosCompat.findPouch(player);
            if (!equipped.isEmpty()) return equipped;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(PouchOfUnknown.POUCH.get())) return stack;
        }
        ItemStack carried = player.containerMenu.getCarried();
        return carried.is(PouchOfUnknown.POUCH.get()) ? carried : ItemStack.EMPTY;
    }

    /** Transfer a restricted inventory stack, dropping only the portion that did not fit. */
    static ItemStack collect(ServerPlayer player, ItemStack stack, ItemStack pouch) {
        ItemStack remainder = pouch.isEmpty() ? stack.copy() : PouchStorage.insert(pouch, stack, player.level());
        if (!remainder.isEmpty() && player.drop(remainder.copy(), false) == null) return remainder;
        return ItemStack.EMPTY;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void tick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player instanceof FakePlayer || player.isCreative() || player.isSpectator()) return;
        ItemStack pouch = findPouch(player);
        var inventory = player.getInventory();
        boolean changed = false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            boolean equipment = i >= inventory.items.size() && i < inventory.items.size() + inventory.armor.size();
            if (equipment ? equipmentLocked(player, stack) : inventoryLocked(player, stack)) {
                inventory.setItem(i, collect(player, stack, pouch));
                changed = true;
            }
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (inventoryLocked(player, carried)) {
            player.containerMenu.setCarried(collect(player, carried, pouch));
            changed = true;
        }
        if (ModList.get().isLoaded("curios")) CuriosCompat.collectLocked(player, pouch);
        if (changed) {
            inventory.setChanged();
            player.containerMenu.broadcastChanges();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void pickup(ItemEntityPickupEvent.Pre event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || player instanceof FakePlayer
                || player.isCreative() || player.isSpectator()) return;
        ItemEntity entity = event.getItemEntity();
        ItemStack stack = entity.getItem();
        if (!isLocked(player, stack)) return;
        event.setCanPickup(TriState.FALSE);
        ItemStack pouch = findPouch(player);
        if (pouch.isEmpty()) {
            entity.setPickUpDelay(20);
            player.displayClientMessage(Component.translatable("pouchofunknown.no_pouch"), true);
            return;
        }
        ItemStack remaining = PouchStorage.insert(pouch, stack, player.level());
        if (remaining.isEmpty()) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        }
        else {
            entity.setItem(remaining);
            entity.setPickUpDelay(20);
            player.displayClientMessage(Component.translatable("pouchofunknown.full"), true);
        }
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}
