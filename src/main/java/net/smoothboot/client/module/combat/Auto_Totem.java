package net.smoothboot.client.module.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.smoothboot.client.module.Mod;
import net.smoothboot.client.module.settings.NumberSetting;

public class Auto_Totem extends Mod {

    public NumberSetting autoTotemSlot = new NumberSetting("Slot", 1, 9, 9, 1);

    public Auto_Totem() {
        super("Auto Totem", "Automatically moves totem into offhand or hotbar slot.", Category.Combat);
        addsettings(autoTotemSlot);
    }

    private boolean isTotem(ItemStack stack) {
        return stack.getItem() == Items.TOTEM_OF_UNDYING;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        // If we already have a totem in offhand, stop
        if (isTotem(mc.player.getOffHandStack())) return;

        // Search inventory for a totem
        for (int slot = 9; slot < 36; slot++) { // 9–35 = main inventory
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (isTotem(stack)) {

                // Move to offhand (slot 45 is offhand)
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 40, SlotActionType.SWAP, mc.player);
                return;
            }
        }

        // Optional: move totem to selected hotbar slot if offhand is not usable
        int selectedHotbarSlot = autoTotemSlot.getValueInt() - 1;
        if (!isTotem(mc.player.getInventory().getStack(selectedHotbarSlot))) {
            for (int slot = 9; slot < 36; slot++) {
                if (isTotem(mc.player.getInventory().getStack(slot))) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, selectedHotbarSlot + 36, SlotActionType.SWAP, mc.player);
                    return;
                }
            }
        }
    }
}
