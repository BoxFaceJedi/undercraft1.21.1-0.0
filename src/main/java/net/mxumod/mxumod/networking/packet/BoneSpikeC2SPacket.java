package net.mxumod.mxumod.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.mxumod.mxumod.skill.special.BoneSpike;

public class BoneSpikeC2SPacket {
    public BoneSpikeC2SPacket() {

    }

    public BoneSpikeC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();

            BoneSpike.boneSpikeAttack(player);

        });
    }
}