package me.nixuge.serverlistbufferfixer.config;

import lombok.Getter;
import me.nixuge.serverlistbufferfixer.McMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Getter
public class ConfigCache {
    private final Configuration configuration;

    private int maxThreadCountPinger;
    private int maxThreadCountTimeout;
    private int serverTimeout;

    public ConfigCache(final Configuration configuration) {
        this.configuration = configuration;
        this.loadConfiguration();
        this.configuration.save();
    }

    private void loadConfiguration() {
        // Enable
        this.maxThreadCountPinger = this.configuration.getInt(
                "Max threadcount (Pinger)",
                "General",
                50,
                5,
                500,
                "Max numbers of threads for the server pinger. Mc defaults to 5, this mods defaults to 50. Those threads are lightweight so shouldn't cause performance issues, and are only used when loading/reloading servers. Requires a Minecraft restart."
        );
        this.maxThreadCountTimeout = this.configuration.getInt(
                "Max threadcount (timeouts)",
                "General",
                100,
                10,
                1000,
                "Max numbers of threads for the server timeout. Usually double of the max threadcount for the server pinger. This mods defaults to 100. Those threads are lightweight so shouldn't cause performance issues, and are only used when loading/reloading servers. Requires a Minecraft restart."
        );
        this.serverTimeout = this.configuration.getInt(
                "Server fetching timeout",
                "General",
                4,
                1,
                60,
                "Timeout for fetching a server's data."
        );

    }

    @SubscribeEvent
    public void onConfigurationChangeEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
        this.configuration.save();
        if (event.modID.equalsIgnoreCase(McMod.MOD_ID)) {
            this.loadConfiguration();
        }
    }
}