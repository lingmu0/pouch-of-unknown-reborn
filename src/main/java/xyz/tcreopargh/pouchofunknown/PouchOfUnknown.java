package xyz.tcreopargh.pouchofunknown;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PouchOfUnknown.MOD_ID)
public final class PouchOfUnknown {
    public static final String MOD_ID = "pouchofunknown";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);
    public static final Supplier<Item> POUCH = ITEMS.register("pouch", PouchItem::new);
    public static final ModConfigSpec.IntValue CAPACITY;
    private static final ModConfigSpec CONFIG;
    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        CAPACITY = builder.comment("Maximum number of item stacks stored in each pouch.")
                .defineInRange("pouchCapacity", 540, 1, 4096);
        CONFIG = builder.build();
    }
    public PouchOfUnknown(IEventBus modBus, ModContainer container) {
        ITEMS.register(modBus);
        modBus.addListener(PouchOfUnknown::addToTab);
        container.registerConfig(ModConfig.Type.COMMON, CONFIG);
        NeoForge.EVENT_BUS.register(new PouchEvents());
    }
    private static void addToTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(POUCH.get());
    }
}
