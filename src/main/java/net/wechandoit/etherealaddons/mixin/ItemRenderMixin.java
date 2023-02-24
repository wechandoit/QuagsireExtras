package net.wechandoit.etherealaddons.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.wechandoit.etherealaddons.QuagsireClient;
import net.wechandoit.etherealaddons.utils.ItemRenderUtils;
import net.wechandoit.etherealaddons.utils.MiscUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class ItemRenderMixin extends Screen {

    protected ItemRenderMixin() {
        super(LiteralText.EMPTY);
        throw new RuntimeException("Mixin constructor called");
    }

    @Inject(method = "drawSlot", at = @At(value = "HEAD"))
    public void renderCustomItemRarity(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        int x = slot.x, y = slot.y;
        MiscUtils.WEAPON_TIER tier = MiscUtils.getTierOfItem(slot.getStack());
        if (tier != MiscUtils.WEAPON_TIER.NULL) {
            int color = MiscUtils.getColorForTier(tier);
            ItemRenderUtils.renderItemRarity(matrices, x, y, color);
        }
    }

    @Inject(method = "drawItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", shift = At.Shift.AFTER))
    public void renderCustomItemText(ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        if (QuagsireClient.onEtherealSkies()) ItemRenderUtils.renderCustomBars(x, y, stack);
    }

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", shift = At.Shift.AFTER))
    public void renderCustomItemText(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (QuagsireClient.onEtherealSkies()) ItemRenderUtils.renderCustomBars(slot.x, slot.y, slot.getStack());
    }

}
