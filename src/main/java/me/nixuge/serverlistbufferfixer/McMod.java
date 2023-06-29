package me.nixuge.serverlistbufferfixer;

import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.Mod;

@Mod(
        modid = McMod.MOD_ID,
        name = McMod.NAME,
        version = McMod.VERSION,
        clientSideOnly = true
)

@Setter
public class McMod {
    public static final String MOD_ID = "serverlistbufferfixer";
    public static final String NAME = "Serverlist Buffer Fixer";
    public static final String VERSION = "1.0.0";


    @Getter
    @Mod.Instance(value = McMod.MOD_ID)
    private static McMod instance;

    //@Mod.EventHandler
    //public void preInit(final FMLPreInitializationEvent event) {
        // Keeping that to setup the config later
        // (timeout & custom messages?)
    //}
}
