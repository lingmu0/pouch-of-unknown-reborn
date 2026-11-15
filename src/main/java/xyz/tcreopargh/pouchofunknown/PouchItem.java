package xyz.tcreopargh.pouchofunknown;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class PouchItem extends Item {
    public PouchItem() { super(new Item.Properties().stacksTo(1)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack pouch = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            int count = PouchStorage.retrieve(pouch, serverPlayer);
            player.displayClientMessage(count == 0 ? Component.translatable("pouchofunknown.nothing")
                    : Component.translatable("pouchofunknown.retrieved", count), true);
        }
        return InteractionResultHolder.sidedSuccess(pouch, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("pouchofunknown.stored", PouchStorage.size(stack)).withStyle(ChatFormatting.YELLOW));
        lines.add(Component.translatable("pouchofunknown.capacity", PouchOfUnknown.CAPACITY.get()).withStyle(ChatFormatting.GRAY));
    }
}
