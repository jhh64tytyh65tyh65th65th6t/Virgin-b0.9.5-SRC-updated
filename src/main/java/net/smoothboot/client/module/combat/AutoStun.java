package net.smoothboot.client.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.smoothboot.client.module.Mod;
import net.smoothboot.client.util.InventoryUtil;

public class AutoStun extends Mod {

    public AutoStun() {
        super("Auto Stun", "Automatically breaks shields using an axe", Category.Combat);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity target = (PlayerEntity) mc.targetedEntity;

        if (targetUsingShield(target)) {
            if (!axeCheck()) {
                selectAxe(); // Try to select one
            }

            if (axeCheck() && mc.player.getAttackCooldownProgress(0) >= 0.25f) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private boolean axeCheck() {
        ItemStack heldItem = mc.player.getMainHandStack();
        return isAxe(heldItem.getItem());
    }

    private void selectAxe() {
        // Stop at first matching axe
        Item[] axes = {
            Items.NETHERITE_AXE,
            Items.DIAMOND_AXE,
            Items.IRON_AXE,
            Items.STONE_AXE,
            Items.GOLDEN_AXE,
            Items.WOODEN_AXE
        };

        for (Item axe : axes) {
            if (InventoryUtil.selectItemFromHotbar(axe)) {
                break;
            }
        }
    }

    private boolean isAxe(Item item) {
        return item == Items.NETHERITE_AXE || item == Items.DIAMOND_AXE ||
               item == Items.IRON_AXE || item == Items.STONE_AXE ||
               item == Items.GOLDEN_AXE || item == Items.WOODEN_AXE;
    }

    private boolean targetUsingShield(PlayerEntity target) {
        return target.isUsingItem() && target.getActiveItem().getItem() == Items.SHIELD;
    }
}
