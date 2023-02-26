package net.wechandoit.etherealaddons.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.wechandoit.etherealaddons.QuagsireClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    private final Pattern welcomePattern = Pattern.compile("➠ Hey, [a-zA-Z0-9_]+.");

    @Inject(method = "onGameMessage", at = @At("TAIL"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ClientPlayerEntity player = QuagsireClient.client.player;
        if (QuagsireClient.onEtherealSkies() && player != null) {
            String message = packet.getMessage().getString();
            if (welcomePattern.matcher(message).matches() && !message.toLowerCase().contains(player.getName().getString().toLowerCase())) {
                ci.cancel();
                player.sendChatMessage("o/");
            }
        }
    }

}
