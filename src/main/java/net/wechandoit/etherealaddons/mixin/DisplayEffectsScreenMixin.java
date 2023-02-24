package net.wechandoit.etherealaddons.mixin;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.wechandoit.etherealaddons.QuagsireClient;
import net.wechandoit.etherealaddons.objects.DummyEffectInstance;
import net.wechandoit.etherealaddons.utils.MiscUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.text.NumberFormat;
import java.util.*;

@Mixin(AbstractInventoryScreen.class)
public abstract class DisplayEffectsScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {

    private static final Identifier COINS = new Identifier(QuagsireClient.MODID, "gui/coins.png");
    private final NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

    public DisplayEffectsScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * @author imtiredrn
     * @reason overwrite existing status effect method to add custom icons
     */
    @Overwrite
    public void drawStatusEffects(MatrixStack matrices, int mouseX, int mouseY) {
        int i = x + backgroundWidth + 2;
        int j = width - i;
        Collection<StatusEffectInstance> collection = client.player.getStatusEffects();
        if (j >= 32) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            boolean wide = j >= 120;
            int k = 33;
            if (collection.size() > 4) {
                k = 132 / (collection.size());
            }

            List<StatusEffectInstance> statusEffects = new ArrayList<>(Ordering.natural().sortedCopy(collection).stream().toList());

            // add dummy icons
            statusEffects.add(0, new DummyEffectInstance(COINS, "Inventory Value", format.format(MiscUtils.getTotalPriceOfClientPlayer())));

            int statusHeight = y;
            for (StatusEffectInstance instance : statusEffects) {
                RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                // Draw background for status effect
                if (wide) {
                    drawTexture(matrices, i, statusHeight, 0, 166, 120, 32);
                } else {
                    drawTexture(matrices, i, statusHeight, 0, 198, 32, 32);
                }

                // draw effect
                if (instance instanceof DummyEffectInstance effectInstance) {
                    RenderSystem.setShaderTexture(0, effectInstance.getIconID());
                    DrawableHelper.drawTexture(matrices, i + (wide ? 6 : 7), statusHeight + 7, 0, 0, 18, 18, 18, 18);

                    if (wide) {
                        textRenderer.drawWithShadow(matrices, effectInstance.getValue(), (float) (i + 10 + 18), (float) (statusHeight + 6), 16777215);
                        textRenderer.drawWithShadow(matrices, effectInstance.getSecondValue(), (float) (i + 10 + 18), (float) (statusHeight + 6 + 10), 8355711);
                    }
                } else {
                    Sprite sprite = client.getStatusEffectSpriteManager().getSprite(instance.getEffectType());
                    RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
                    drawSprite(matrices, i + (wide ? 6 : 7), statusHeight + 7, getZOffset(), 18, 18, sprite);

                    if (wide) {
                        textRenderer.drawWithShadow(matrices, getStatusEffectDescription(instance), (float) (i + 10 + 18), (float) (statusHeight + 6), 16777215);
                        textRenderer.drawWithShadow(matrices, StatusEffectUtil.durationToString(instance, 1.0F), (float) (i + 10 + 18), (float) (statusHeight + 6 + 10), 8355711);
                    } else if (mouseX >= i && mouseX <= i + 33) {
                        renderTooltip(matrices, List.of(getStatusEffectDescription(instance), new LiteralText(StatusEffectUtil.durationToString(instance, 1.0F))), Optional.empty(), mouseX, mouseY);
                    }
                }
                statusHeight += k;
            }

        }
    }

    private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
        MutableText mutableText = statusEffect.getEffectType().getName().shallowCopy();
        if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
            MutableText text = mutableText.append(" ");
            int amplifier = statusEffect.getAmplifier();
            text.append(new TranslatableText("enchantment.level." + (amplifier + 1)));
        }

        return mutableText;
    }
}
