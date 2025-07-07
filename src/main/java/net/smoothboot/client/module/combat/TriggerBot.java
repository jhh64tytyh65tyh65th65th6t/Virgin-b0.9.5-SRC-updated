package net.smoothboot.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.smoothboot.client.module.Mod;
import net.smoothboot.client.module.settings.BooleanSetting;
import net.smoothboot.client.module.settings.NumberSetting;

public class TriggerBot extends Mod {

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public NumberSetting triggerbotdelay = new NumberSetting("Delay", 0, 1, 0.1, 0.01);
    public BooleanSetting triggerbotItem = new BooleanSetting("Check item", false);
    public BooleanSetting triggerbotSword = new BooleanSetting("Check sword", true);
    public BooleanSetting triggerbotTeam = new BooleanSetting("Team check", true);
    public BooleanSetting triggerbotCrit = new BooleanSetting("Critical timing", false);

    public TriggerBot() {
        super("Trigger Bot", "Hits target when aiming at them", Category.Combat);
        addsettings(triggerbotdelay, triggerbotItem, triggerbotSword, triggerbotTeam, triggerbotCrit);
    }

    @Override
    public void onTick() {
        if (nullCheck() || mc.player == null || mc.interactionManager == null || mc.crosshairTarget == null) return;

        Entity target = getTarget();
        if (target == null || !isValid(target)) return;

        // Check if blocking (for some servers)
        if (triggerbotItem.isEnabled() && mc.player.isUsingItem()) return;

        if (triggerbotSword.isEnabled() && !swordCheck()) return;

        if (triggerbotTeam.isEnabled() && target.isTeammate(mc.player)) return;

        if (triggerbotCrit.isEnabled() && (mc.player.isOnGround() || mc.player.fallDistance <= 0.01f)) return;

        float cooldown = mc.player.getAttackCooldownProgress(0);

        if (cooldown >= triggerbotdelay.getValue()) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private Entity getTarget() {
        if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) mc.crosshairTarget).getEntity();
        }
        return null;
    }

    private boolean swordCheck() {
        ItemStack item = mc.player.getMainHandStack();
        return item.getItem() == Items.NETHERITE_SWORD
            || item.getItem() == Items.DIAMOND_SWORD
            || item.getItem() == Items.IRON_SWORD
            || item.getItem() == Items.STONE_SWORD
            || item.getItem() == Items.GOLDEN_SWORD
            || item.getItem() == Items.WOODEN_SWORD;
    }

    private boolean isValid(Entity entity) {
        return entity instanceof PlayerEntity && entity.isAlive() && !entity.isInvisible();
    }
}
