package net.wechandoit.etherealaddons.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.wechandoit.etherealaddons.QuagsireClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleSpawnMixin {

    MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"))
    public void addParticles(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (QuagsireClient.onEtherealSkies() && parameters.getType() == ParticleTypes.ANGRY_VILLAGER) {
            client.getToastManager().clear();
            client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.of("There is a mantis nearby!"), Text.of("look around...")));
        }
    }
}
