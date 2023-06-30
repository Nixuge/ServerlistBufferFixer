package me.nixuge.serverlistbufferfixer.gui;

import me.nixuge.serverlistbufferfixer.McMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig(final GuiScreen guiScreen) {
        super(
                guiScreen,
                new ConfigElement(McMod.getInstance().getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                McMod.MOD_ID,
                false,
                false,
                McMod.NAME + " Configuration"
        );
    }
}