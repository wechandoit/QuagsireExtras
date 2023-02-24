package net.wechandoit.etherealaddons.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.wechandoit.etherealaddons.QuagsireClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {


    @Redirect(method = "renderWeather", at  = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;doesNotSnow(Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean doesNotSnow(Biome instance, BlockPos pos) {
        return !QuagsireClient.season.equals("Winter") || instance.getTemperature() >= 0.15F;
    }

}
