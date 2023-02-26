package net.wechandoit.etherealaddons;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wechandoit.etherealaddons.utils.DiscordUtils;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Environment(EnvType.CLIENT)
public class QuagsireClient implements ClientModInitializer {

    public static MinecraftClient client = MinecraftClient.getInstance();
    public static IPCClient ipcClient = new IPCClient(1074189578291912734L);
    public static final String MODID = "etherealaddons";
    private static int clientTick = 1;
    private static int discordAppCount = 0;
    private static String previousIP = "";
    public static String location = "";
    public static int playerLevel = 0;
    public static boolean onIsland = false;

    public static String season = "";
    public static int day = -1;
    public static int hour = -1;
    public static int minutes = -1;

    public static int fullyGrownCabbageCount = 0;
    public static int fullyGrownTomatoCount = 0;
    public static int fullyGrownPepperCount = 0;
    public static int fullyGrownCornCount = 0;
    public static int fullyGrownGrapeCount = 0;
    public static int fullyGrownGarlicCount = 0;


    @Override
    public void onInitializeClient() {
        System.out.println("QuagsireExtras - Made by wechandoit! (V1.0)");
        System.out.println("Support Discord: https://discord.gg/2jxF3Ed9FP");

        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> runnableRunner());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> setupIPC(client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> closeIPC());
    }

    public static boolean onEtherealSkies() {
        return !client.isInSingleplayer() && client.getCurrentServerEntry().address.contains("etherealskies.com");
    }

    public void runnableRunner() {
        clientTick++;

        if (clientTick > 20) {
            clientTick = 1;
            if (onEtherealSkies() && onIsland) {
                countCrops(client.player.getWorld());
            }
        }
        else if (clientTick == 20) {
            discordAppCount++;
            if (discordAppCount > 5) discordAppCount = 0;
            else if (discordAppCount == 5) {
                if (onEtherealSkies()) {
                    if (location.equals("")) {
                        DiscordUtils.updateRPC("Player level: " + playerLevel, "Loading...");
                    } else {
                        DiscordUtils.updateRPC("Player level: " + playerLevel, location);
                    }
                } else {
                    if (!MinecraftClient.getInstance().isInSingleplayer()) DiscordUtils.updateRPC("In Game Menu", "");
                }
            }
        }
    }

    public void closeIPC() {
        if (onEtherealSkies()) {
            previousIP = "";
            ipcClient.close();
        }
    }

    public void setupIPC(MinecraftClient client) {
        clientTick = 1;
        if (onEtherealSkies()) {
            if (previousIP.equals("")) {
                previousIP = client.getCurrentServerEntry().address;
                try {
                    ipcClient.connect();
                } catch (NoDiscordClientException e) {
                    e.printStackTrace();
                }
                DiscordUtils.lastTimestamp = OffsetDateTime.now().toEpochSecond();
            }
            DiscordUtils.updateRPC("", "");
        }
    }

    // find way to optimize this more in the future
    public void countCrops(World world) {
        if (world.isClient) {
            ClientPlayerEntity player = client.player;
            int startX = ((player.getChunkPos().x + 1) * 16) - 1, startY = Math.max(-64, player.getBlockY()-1), startZ = player.getChunkPos().z * 16;
            AtomicIntegerArray cropCounts = new AtomicIntegerArray(6);

            for (int y = startY; y < Math.min(320, player.getBlockY() + 1) + 1; y++) {
                for (int x = startX; x > startX - 16; x--) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        BlockState state = world.getBlockState(new BlockPos(x, y, z));
                        if (state.getBlock() instanceof TripwireBlock) {
                            // cabbage
                            if (state.get(TripwireBlock.NORTH) && state.get(TripwireBlock.SOUTH) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.WEST) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.DISARMED) || state.get(TripwireBlock.POWERED))) {
                                cropCounts.incrementAndGet(0);
                            }
                            // tomato
                            if (state.get(TripwireBlock.NORTH) && state.get(TripwireBlock.SOUTH) && state.get(TripwireBlock.DISARMED) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.WEST) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.POWERED))) {
                                cropCounts.incrementAndGet(1);
                            }
                            // pepper
                            if (state.get(TripwireBlock.WEST) && state.get(TripwireBlock.SOUTH) && state.get(TripwireBlock.DISARMED) && state.get(TripwireBlock.POWERED) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.NORTH))) {
                                cropCounts.incrementAndGet(2);
                            }
                            // corn
                            if (state.get(TripwireBlock.WEST) && state.get(TripwireBlock.SOUTH) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.NORTH) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.DISARMED) || state.get(TripwireBlock.POWERED))) {
                                cropCounts.incrementAndGet(3);
                            }
                            // grape
                            if (state.get(TripwireBlock.DISARMED) && state.get(TripwireBlock.WEST) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.NORTH) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.SOUTH) || state.get(TripwireBlock.POWERED))) {
                                cropCounts.incrementAndGet(4);
                            }
                            // garlic
                            if (state.get(TripwireBlock.NORTH) && state.get(TripwireBlock.DISARMED) &&
                                    !(state.get(TripwireBlock.EAST) || state.get(TripwireBlock.WEST) || state.get(TripwireBlock.ATTACHED) || state.get(TripwireBlock.SOUTH) || state.get(TripwireBlock.POWERED))) {
                                cropCounts.incrementAndGet(5);
                            }
                        }
                    }
                }
            }
            fullyGrownCabbageCount = cropCounts.get(0);
            fullyGrownTomatoCount = cropCounts.get(1);
            fullyGrownPepperCount = cropCounts.get(2);
            fullyGrownCornCount = cropCounts.get(3);
            fullyGrownGrapeCount = cropCounts.get(4);
            fullyGrownGarlicCount = cropCounts.get(5);
        }
    }


}
