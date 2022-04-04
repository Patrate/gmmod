package fr.emmuliette.gmmod.packets;

import java.util.function.Supplier;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SheetPacket {
	private final CompoundTag nbt;

	public SheetPacket(CompoundTag tag) {
		this.nbt = tag;
	}

	public static void encode(SheetPacket msg, FriendlyByteBuf buf) {
		buf.writeNbt(msg.nbt);
	}

	public static SheetPacket decode(FriendlyByteBuf buf) {
		return new SheetPacket(buf.readNbt());
	}

	public static class Handler {
		public static void handle(final SheetPacket msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				PacketHandler.minecraft.player.getCapability(CharacterSheet.SHEET_CAPABILITY)
						.ifPresent(cap -> cap.deserializeNBT(msg.nbt));
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
