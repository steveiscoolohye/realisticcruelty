package com.moskowitz.realisticcruelty.network;

import com.moskowitz.realisticcruelty.CruelMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GorePacket(
        int entityId, int directId, HitType hitType, 
        double x, double y, double z, 
        double xd, double yd, double zd, 
        float amount
) implements CustomPacketPayload {

    // 1. Define the unique ID for this packet
    public static final CustomPacketPayload.Type<GorePacket> TYPE = 
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CruelMod.MOD_ID, "gore_packet"));

    // 2. The StreamCodec replaces the old write and read methods
    public static final StreamCodec<FriendlyByteBuf, GorePacket> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeVarInt(msg.entityId);
                buf.writeVarInt(msg.directId);
                buf.writeEnum(msg.hitType);
                buf.writeDouble(msg.x);
                buf.writeDouble(msg.y);
                buf.writeDouble(msg.z);
                buf.writeDouble(msg.xd);
                buf.writeDouble(msg.yd);
                buf.writeDouble(msg.zd);
                buf.writeFloat(msg.amount);
            },
            buf -> new GorePacket(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readEnum(HitType.class),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // 3. The Handler: No more DistExecutor!
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // This ensures the code only runs on the client (where blood particles exist)
            if (context.flow().isClientbound()) {
                HandleGore.handle(this);
            }
        });
    }
}
