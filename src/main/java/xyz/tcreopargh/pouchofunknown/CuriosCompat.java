package xyz.tcreopargh.pouchofunknown;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

/** Only loaded after checking that Curios is present. */
final class CuriosCompat {
    private CuriosCompat() {}

    static ItemStack findPouch(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(PouchOfUnknown.POUCH.get()))
                .map(SlotResult::stack).orElse(ItemStack.EMPTY);
    }

    static void collectLocked(ServerPlayer player, ItemStack pouch) {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            var slots = curios.getEquippedCurios();
            for (int i = 0; i < slots.getSlots(); i++) {
                ItemStack stack = slots.getStackInSlot(i);
                if (PouchEvents.equipmentLocked(player, stack)) {
                    slots.setStackInSlot(i, PouchEvents.collect(player, stack, pouch));
                }
            }
        });
    }
}
