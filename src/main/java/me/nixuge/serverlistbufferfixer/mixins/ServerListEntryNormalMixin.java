package me.nixuge.serverlistbufferfixer.mixins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.ServerListEntryNormal;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.UnknownHostException;
import java.util.concurrent.*;

@Mixin(ServerListEntryNormal.class)
public class ServerListEntryNormalMixin {
    @Shadow
    @Final
    private
    GuiMultiplayer owner;
    @Shadow
    @Final
    private ServerData server;

    @Shadow
    private static ThreadPoolExecutor field_148302_b;
    // Note: if servers are added, this will be inaccurate
    // But it should be good enough still
    // Can't bother to mixin onto some other classes just to change that (rn at least).
    private static final int serverCountCache;
    static {
        serverCountCache = new ServerList(Minecraft.getMinecraft()).countServers();
        // Note: not even sure this reassignement works since the field is final
        field_148302_b = new ScheduledThreadPoolExecutor(serverCountCache + 5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    }
    private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(serverCountCache * 2 + 5);

    private static int runningTaskCount = 0;

    private Runnable getPingTask() {
        return new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                owner.getOldServerPinger().ping(server);
            }
        };
    }

    private void setServerFail(String error) {
        server.pingToServer = -1L;
        server.serverMOTD = error;
    }

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"))
    public Future<?> drawEntry(ThreadPoolExecutor instance, Runnable r) {
        // Check if too many running tasks, if yes cancel & set to "spamming"
        if (runningTaskCount > serverCountCache * 2) {
            setServerFail(EnumChatFormatting.GRAY + "Spamming...");
            return field_148302_b.submit(() -> {});
        }

        // Start up the timeout task
        final Future<?> future = timeoutExecutor.submit(getPingTask());
        runningTaskCount++;

        // "Vanilla" behavior, modified to:
        // - use a timeout for the task instead of the ping directly
        // - handle future.get()'s exceptions instead of the ping's exceptions
        return field_148302_b.submit(new Runnable() {
            public void run() {
                try {
                    future.get(4, TimeUnit.SECONDS);
                } catch (TimeoutException e1) {
                    setServerFail(EnumChatFormatting.RED + "Timed out");
                } catch (ExecutionException e2) {
                    if (e2.getCause() instanceof UnknownHostException) {
                        setServerFail(EnumChatFormatting.DARK_RED + "Can't resolve hostname");
                        setServerFail(EnumChatFormatting.GRAY + "Spamming...");
                    }
                    else
                        setServerFail(EnumChatFormatting.DARK_RED + "Can't connect to server.");

                } catch (Exception e3) {
                    // Shouldn't happen anymore but just in case
                    setServerFail(EnumChatFormatting.DARK_RED + "Can't connect to server.");
                }
                runningTaskCount--;
            }
        });
    }
}
