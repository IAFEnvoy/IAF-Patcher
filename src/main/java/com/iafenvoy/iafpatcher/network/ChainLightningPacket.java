package com.iafenvoy.iafpatcher.network;

import com.iafenvoy.iafpatcher.IceAndFirePatcher;
import com.iafenvoy.iafpatcher.client.WeaponLightningEffects;
import io.netty.handler.codec.DecoderException;
//? >=1.17 {
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
//?} else {
/*import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
*///?}
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/** One bounded batch of fixed world-space endpoints; no entity state is trusted from a client. */
public final class ChainLightningPacket {
    public static final int MAX_LINKS = 1023;
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(IceAndFirePatcher.MOD_ID, "weapon_lightning"), () -> "1", "1"::equals, "1"::equals);
    public final String dimension;
    public final List<Link> links;

    public ChainLightningPacket(String dimension, List<Link> links) {
        if (links.size() > MAX_LINKS) throw new IllegalArgumentException("Too many lightning links");
        this.dimension = dimension;
        this.links = Collections.unmodifiableList(new ArrayList<>(links));
    }

    public static void register() {
        CHANNEL.registerMessage(0, ChainLightningPacket.class, ChainLightningPacket::encode,
                ChainLightningPacket::decode, ChainLightningPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void send(/*? >=1.17 {*/ServerLevel/*?} else {*//*ServerWorld*//*?}*/ level, LivingEntity attacker, List<Link> links) {
        ChainLightningPacket packet = new ChainLightningPacket(level.dimension().location().toString(), links);
        for (/*? >=1.17 {*/ServerPlayer/*?} else {*//*ServerPlayerEntity*//*?}*/ player : level.getPlayers(player -> player.distanceToSqr(attacker) < 64 * 64))
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void encode(ChainLightningPacket packet, /*? >=1.17 {*/FriendlyByteBuf/*?} else {*//*PacketBuffer*//*?}*/ buffer) {
        buffer.writeUtf(packet.dimension);
        buffer.writeVarInt(packet.links.size());
        for (Link link : packet.links) {
            buffer.writeDouble(link.x1).writeDouble(link.y1).writeDouble(link.z1);
            buffer.writeDouble(link.x2).writeDouble(link.y2).writeDouble(link.z2);
        }
    }

    public static ChainLightningPacket decode(/*? >=1.17 {*/FriendlyByteBuf/*?} else {*//*PacketBuffer*//*?}*/ buffer) {
        String dimension = buffer.readUtf(256);
        int count = buffer.readVarInt();
        if (count < 0 || count > MAX_LINKS || buffer.readableBytes() < count * 48)
            throw new DecoderException("Invalid lightning link count: " + count);
        List<Link> links = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double[] coordinates = new double[6];
            for (int j = 0; j < 6; j++) {
                coordinates[j] = buffer.readDouble();
                if (!Double.isFinite(coordinates[j])) throw new DecoderException("Non-finite lightning endpoint");
            }
            links.add(new Link(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]));
        }
        return new ChainLightningPacket(dimension, links);
    }

    private static void handle(ChainLightningPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> WeaponLightningEffects.receive(packet)));
        context.setPacketHandled(true);
    }

    public static final class Link {
        public final double x1, y1, z1, x2, y2, z2;

        public Link(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
    }
}
