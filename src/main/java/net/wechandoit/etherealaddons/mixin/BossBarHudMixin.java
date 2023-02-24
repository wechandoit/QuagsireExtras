package net.wechandoit.etherealaddons.mixin;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.wechandoit.etherealaddons.QuagsireClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    /*
      Replace with regex in the future
     */
    @Inject(method = "renderBossBar", at = @At("TAIL"))
    public void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) {
        String bossbar_unparsed = bossBar.getName().getString();
        String[] bossbar_split = bossbar_unparsed.split(" ");


        if (bossbar_unparsed.contains("Ethereal Island")) {
            QuagsireClient.location = "Spawn";
            QuagsireClient.onIsland = false;
        } else if (bossbar_unparsed.contains("Island")) {
            QuagsireClient.location = "On Island";
            QuagsireClient.onIsland = true;
        } else if (bossbar_unparsed.contains("The Golden Woods")) {
            QuagsireClient.location = "The Golden Woods";
            QuagsireClient.onIsland = false;
        } else if (bossbar_unparsed.contains("The Forest Ruins")) {
            QuagsireClient.location = "The Forest Ruins";
            QuagsireClient.onIsland = false;
        } else if (bossbar_unparsed.contains("The Shifting Sands")) {
            QuagsireClient.location = "The Shifting Sands";
            QuagsireClient.onIsland = false;
        } else if (bossbar_unparsed.contains("The Underworld Fortress")) {
            QuagsireClient.location = "The Underworld Fortress";
            QuagsireClient.onIsland = false;
        } else if (bossbar_unparsed.contains("The High Heavens")) {
            QuagsireClient.location = "The High Heavens";
            QuagsireClient.onIsland = false;
        }

        if (bossbar_split.length == 9) {
            String time_of_day = bossbar_split[8];
            if (time_of_day.equalsIgnoreCase("AM") || time_of_day.equalsIgnoreCase("PM") && bossbar_split[3].equalsIgnoreCase("Day")) {
                try {
                    QuagsireClient.day = Integer.parseInt(bossbar_split[4].substring(0, bossbar_split[4].length() - 1));
                    QuagsireClient.season = bossbar_split[2].substring(1);
                    String[] time = bossbar_split[7].replaceAll("§f", "").split(":");
                    QuagsireClient.hour = Integer.parseInt(time[0]);
                    if (time_of_day.equalsIgnoreCase("PM") && QuagsireClient.hour < 12)
                        QuagsireClient.hour += 12;
                    QuagsireClient.minutes = Integer.parseInt(time[1]);
                } catch (Exception e) {
                    System.out.println(bossbar_unparsed);
                }
            }
        }


    }

}
