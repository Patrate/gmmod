package fr.emmuliette.gmmod.characterSheet;



import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SheetPacket {
	private final CompoundTag nbt;

	public SheetPacket(CompoundTag tag) {
		this.nbt = tag;
	}

	public static void encode(SheetPacket msg, FriendlyByteBuf buf) {
		buf.writeNbt((CompoundTag) msg.nbt);
	}

	public static SheetPacket decode(FriendlyByteBuf buf) {
		return new SheetPacket(buf.readNbt());
	}

	public static class Handler {
		public static void handle(final SheetPacket msg, Supplier<NetworkEvent.Context> ctx) {
//			System.out.println("HANDLING PACKET");
			Minecraft minecraft = Minecraft.getInstance();

			ctx.get().enqueueWork(() -> {
				minecraft.player.getCapability(SheetCapability.SHEET_CAPABILITY).ifPresent(cap -> cap.fromNBT(msg.nbt));

			});
			ctx.get().setPacketHandled(true);
		}
	}
}
