package net.team.mxumod.minecraftxundertale.event;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.team.mxumod.minecraftxundertale.Minecraftxundertale;
import net.team.mxumod.minecraftxundertale.networking.ModMessages;
import net.team.mxumod.minecraftxundertale.networking.packet.BoneBarrageC2SPacket;
import net.team.mxumod.minecraftxundertale.networking.packet.BoneSpikeC2SPacket;
import net.team.mxumod.minecraftxundertale.networking.packet.BoneWallC2SPacket;
import net.team.mxumod.minecraftxundertale.skill.CameraLock;
import net.team.mxumod.minecraftxundertale.skill.PlayerSkillManager;
import net.team.mxumod.minecraftxundertale.skill.dodge.SideStepSkill;
import net.team.mxumod.minecraftxundertale.util.Keybinding;


public class ClientEvents {
    private static final long DODGE_COOLDOWN_MS = 500;
    private static long lastDodgeTime = 0;

    @EventBusSubscriber(modid = Minecraftxundertale.MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        private static final Minecraft minecraft = Minecraft.getInstance();

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (minecraft.player != null) {
                long currentTime = System.currentTimeMillis();
                if (Keybinding.COMBAT_MODE.consumeClick()) {
                    if (!EnterCombatmode.isCombatmode()) {
                        EnterCombatmode.enterCombatmode();
                    }else {
                        EnterCombatmode.leaveCombatmode();
                    }
                } else if (EnterCombatmode.isCombatmode()) {
                    if (Keybinding.DODGE.consumeClick() && !(currentTime - lastDodgeTime < DODGE_COOLDOWN_MS)) {
                        if (minecraft.player.getInventory().selected == 0 && minecraft.player.onGround()) {
                            new PlayerSkillManager().activateSkill(new SideStepSkill().getName(), minecraft.player);
                        }
                        lastDodgeTime = currentTime;
                    }else if (Keybinding.SPECIAL_ATTACK.consumeClick()) {
                        if (minecraft.player.getInventory().selected == 0) {
                            ModMessages.sendToServer((new BoneSpikeC2SPacket()));
                        }
                    }else if (Keybinding.ULTIMATE_ATTACK.consumeClick()) {
                        minecraft.player.sendSystemMessage(Component.literal("ult attack"));
                    }
                }else {
                    if (Keybinding.SETTINGS.consumeClick()) {

                    }
                }
            }
        }
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event) {
            if (minecraft.player != null) {
                if (EnterCombatmode.isCombatmode()) {
                    if (minecraft.player.getInventory().selected == 0) {
                        if (Keybinding.BASIC_ATTACK.isDown() && !Keybinding.BLOCKING.isDown()) {
                            ModMessages.sendToServer(new BoneBarrageC2SPacket());
                        }
                    }
                }
            }
        }
        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton event) {
            if (minecraft.player != null) {
                if (minecraft.player.getInventory().selected == 0) {
                    if (Keybinding.BLOCKING.isDown() && EnterCombatmode.isCombatmode() && !Keybinding.BASIC_ATTACK.isDown()) {
                        ModMessages.sendToServer(new BoneWallC2SPacket());
                    }
                }
                if (Keybinding.LOCK_ON.consumeClick() && EnterCombatmode.isCombatmode()) {
                    CameraLock.toggleCameraLock(minecraft.player);
                }
            }
        }
        @SubscribeEvent
        public static void onPlayerDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof Player) {
                EnterCombatmode.leaveCombatmode();
            }
        }
    }

    @EventBusSubscriber(modid = Minecraftxundertale.MODID, value = Dist.CLIENT,bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public  static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(Keybinding.COMBAT_MODE);
            event.register(Keybinding.DODGE);
            event.register(Keybinding.SPECIAL_ATTACK);
            event.register(Keybinding.ULTIMATE_ATTACK);
            event.register(Keybinding.BASIC_ATTACK);
            event.register(Keybinding.BLOCKING);
            event.register(Keybinding.LOCK_ON);
            event.register(Keybinding.SETTINGS);
        }
    }

}
