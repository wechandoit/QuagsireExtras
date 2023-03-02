package net.wechandoit.etherealaddons.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.wechandoit.etherealaddons.QuagsireClient;

public class MiscUtils {

    static MinecraftClient client = QuagsireClient.client;

    public static int getWateringCanAmount(ItemStack stack) {
        if (isWateringCan(stack)) {
            return stack.getNbt().getInt("WaterAmount");
        }
        return -1;
    }

    public static boolean isWateringCan(ItemStack stack) {
        if (stack != null && stack.hasCustomName()) {
            return stack.getName().getString().contains("Watering Can");
        }
        return false;
    }

    public static boolean isGrappleGun(ItemStack stack) {
        if (stack != null && stack.hasCustomName()) {
            return stack.getName().getString().contains("Grapple Gun");
        }
        return false;
    }

    public static int getGrappleGunUses(ItemStack stack) {
        if (isGrappleGun(stack)) {
            NbtCompound compound = stack.getNbt().getCompound("PublicBukkitValues");
            return compound.getInt("grapplinghook:uses");
        }
        return -1;
    }

    public static int getMaxGrappleGunUses(ItemStack stack) {
        if (isGrappleGun(stack)) {
            NbtCompound compound = stack.getNbt().getCompound("PublicBukkitValues");
            String type =  compound.getString("grapplinghook:id");
            if (type.equals("grapple_gun")) return 100;
            else if (type.equals("grapple_gun2")) return 1000;
        }
        return -1;
    }

    public static boolean isSellable(ItemStack stack) {
        if (stack != null && stack.hasNbt()) {
            return stack.getNbt().get("Price") != null;
        }
        return false;
    }

    public static double getPrice(ItemStack stack) {
        if (stack != null && stack.hasNbt() && isSellable(stack)) {
            return stack.getNbt().getDouble("Price");
        }
        return 0.0;
    }

    public static double getTotalPriceOfClientPlayer() {
        double price = 0.0;
        for (ItemStack itemStack : client.player.getInventory().main) {
            price += (getPrice(itemStack) * itemStack.getCount());
        }
        return price;
    }

    public static WEAPON_TIER getTierOfItem(ItemStack stack) {
        if (stack != null && stack.hasNbt()) {
            NbtCompound display = stack.getOrCreateNbt().getCompound(ItemStack.DISPLAY_KEY);
            NbtList list = display.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);
            if (list != null && !list.isEmpty()) {
                String unchecked_tier = list.get(0).toString();
                if (unchecked_tier.contains("\uE0F3")) {
                    return WEAPON_TIER.COMMON;
                } else if (unchecked_tier.contains("\uE0F7")) {
                    return WEAPON_TIER.RARE;
                } else if (unchecked_tier.contains("\uE0F4")) {
                    return WEAPON_TIER.EPIC;
                } else if (unchecked_tier.contains("\uE0F5")) {
                    return WEAPON_TIER.LEGENDARY;
                } else if (unchecked_tier.contains("\uE0F6")) {
                    return WEAPON_TIER.PRISMATIC;
                } else if (unchecked_tier.contains("\uE0F2")) {
                    return WEAPON_TIER.ANCIENT;
                } else if (unchecked_tier.contains("\uE226")) {
                    return WEAPON_TIER.EVENT;
                }
            }
        }
        return WEAPON_TIER.NULL;
    }

    public static int getColorForTier(WEAPON_TIER tier) {
        return switch (tier) {
            case COMMON -> ItemRenderUtils.RGBA(127, 127, 127, 255);
            case RARE -> ItemRenderUtils.RGBA(230, 189, 47, 255);
            case EPIC -> ItemRenderUtils.RGBA(47, 133, 230, 255);
            case LEGENDARY -> ItemRenderUtils.RGBA(239, 121, 16, 255);
            case PRISMATIC -> ItemRenderUtils.RGBA(255, 255, 255, 255);
            case ANCIENT -> ItemRenderUtils.RGBA(250, 7, 72, 255);
            case EVENT -> ItemRenderUtils.RGBA(165, 65, 222, 255);
            default -> 0;
        };
    }

    public static boolean isCloudyGem(ItemStack stack) {
        if (stack != null && stack.hasCustomName()) {
            return stack.getName().getString().contains("Cloudy Gem");
        }
        return false;
    }

    public static boolean isMaxedCloudlyGem(ItemStack stack) {
        if (isCloudyGem(stack)) {
            return stack.getName().getString().contains("(Ready)");
        }
        return false;
    }

    public static int getGemSouls(ItemStack stack) {
        if (isCloudyGem(stack)) {
            NbtCompound compound = stack.getNbt().getCompound("MissionPouches");
            NbtCompound progressCompound = compound.getCompound("Progress");
            return (int) progressCompound.getLong("chargeGem");
        }
        return -1;
    }

    public enum WEAPON_TIER {
        NULL, COMMON, RARE, EPIC, LEGENDARY, PRISMATIC, ANCIENT, EVENT
    }

    public static int getMaxWateringCanAmount(ItemStack stack) {
        if (stack.getName().getString().contains("Copper")) return 6;
        if (stack.getName().getString().contains("Steel")) return 8;
        if (stack.getName().getString().contains("Golden")) return 10;
        if (stack.getName().getString().contains("Demonite")) return 16;
        else return 0;
    }


}
