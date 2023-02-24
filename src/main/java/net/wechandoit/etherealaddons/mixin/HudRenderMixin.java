package net.wechandoit.etherealaddons.mixin;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.wechandoit.etherealaddons.QuagsireClient;
import net.wechandoit.etherealaddons.objects.DummyEffectInstance;
import net.wechandoit.etherealaddons.utils.ItemRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class HudRenderMixin extends DrawableHelper {

    private MinecraftClient client = QuagsireClient.client;
    private static final Identifier CABBAGE = new Identifier(QuagsireClient.MODID, "gui/cabbage.png");
    private static final Identifier TOMATO = new Identifier(QuagsireClient.MODID, "gui/tomato.png");
    private static final Identifier PEPPER = new Identifier(QuagsireClient.MODID, "gui/pepper.png");
    private static final Identifier CORN = new Identifier(QuagsireClient.MODID, "gui/corn.png");
    private static final Identifier GRAPE = new Identifier(QuagsireClient.MODID, "gui/grape.png");
    private static final Identifier GARLIC = new Identifier(QuagsireClient.MODID, "gui/garlic.png");

    @Inject(method = "renderHotbarItem", at = @At(value = "TAIL"))
    public void renderCustomItemText(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (QuagsireClient.onEtherealSkies()) ItemRenderUtils.renderCustomBars(x, y, stack);
    }

    @Inject(method = "setOverlayMessage", at = @At(value = "TAIL"))
    public void getActionbar(Text message, boolean tinted, CallbackInfo ci) {
        if (QuagsireClient.onEtherealSkies()) {
            String actionbarText_unparsed = message.getString().replaceAll("[^\\x00-\\x7F]", "-");
            try {
                String level = actionbarText_unparsed.split(" ")[1].substring(3).replaceAll("-", "");
                QuagsireClient.playerLevel = Integer.parseInt(level);
            } catch (Exception e) {
                // ignored
            }
        }
    }

    /**
     * @author imtiredrn
     * @reason overwrite existing status effect method to add custom icons
     */
    @Overwrite
    public void renderStatusEffectOverlay(MatrixStack matrices) {
        // Replicate vanilla placement algorithm to get the duration
        // labels to line up exactly right.

        RenderSystem.enableBlend();
        int beneficialCount = 0;
        int nonBeneficialCount = 0;
        StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
        List<StatusEffectInstance> statusEffects = new ArrayList<>(Ordering.natural().sortedCopy(client.player.getStatusEffects()).stream().toList());
        // test add custom icon
        if (QuagsireClient.onEtherealSkies() && QuagsireClient.onIsland) {
            statusEffects.add(0, new DummyEffectInstance(CABBAGE, QuagsireClient.fullyGrownCabbageCount));
            statusEffects.add(1, new DummyEffectInstance(TOMATO, QuagsireClient.fullyGrownTomatoCount));
            statusEffects.add(2, new DummyEffectInstance(PEPPER, QuagsireClient.fullyGrownPepperCount));
            statusEffects.add(3, new DummyEffectInstance(CORN, QuagsireClient.fullyGrownCornCount));
            statusEffects.add(4, new DummyEffectInstance(GRAPE, QuagsireClient.fullyGrownGrapeCount));
            statusEffects.add(5, new DummyEffectInstance(GARLIC, QuagsireClient.fullyGrownGarlicCount));

        }

        if (!statusEffects.isEmpty()) {
            for (StatusEffectInstance statusEffectInstance : statusEffects) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                int x = client.getWindow().getScaledWidth();
                int y = 1;
                if (statusEffectInstance instanceof DummyEffectInstance instance) {
                    beneficialCount++;
                    x -= 25 * beneficialCount;

                    RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    drawTexture(matrices, x, y, 165, 166, 24, 24);

                    RenderSystem.setShaderTexture(0, instance.getIconID());
                    drawTexture(matrices, x + 3, y + 3, 0, 0, 18, 18, 18, 18);

                    int durationLength = client.textRenderer.getWidth(instance.getValue());
                    drawStringWithShadow(matrices, client.textRenderer, instance.getValue(), x + 13 - (durationLength / 2), y + 14, 0x99FFFFFF);
                } else if (statusEffectInstance.shouldShowIcon()) {
                    if (client.isDemo()) {
                        y += 15;
                    }

                    if (statusEffect.isBeneficial()) {
                        beneficialCount++;
                        x -= 25 * beneficialCount;
                    } else {
                        nonBeneficialCount++;
                        x -= 25 * nonBeneficialCount;
                        y += 26;
                    }

                    RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;
                    if (statusEffectInstance.isAmbient()) {
                        drawTexture(matrices, x, y, 165, 166, 24, 24);
                    } else {
                        drawTexture(matrices, x, y, 141, 166, 24, 24);
                        if (statusEffectInstance.getDuration() <= 200) {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            f = MathHelper.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f);
                    drawSprite(matrices, x + 3, y + 3, getZOffset(), 18, 18, sprite);

                    int amplifier = statusEffectInstance.getAmplifier();
                    if (amplifier > 0) {
                        // Most langages has "translations" for amplifier 1-5, converting to roman numerals
                        String amplifierString = (amplifier < 6) ? I18n.translate("potion.potency." + amplifier) : "**";
                        int amplifierLength = client.textRenderer.getWidth(amplifierString);
                        drawStringWithShadow(matrices, client.textRenderer, amplifierString, x + 22 - amplifierLength, y + 3, 0x99FFFFFF);
                    }
                }
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    }
}
