package fr.emmuliette.gmmod;

import java.util.List;

import fr.emmuliette.gmmod.characterSheet.ISheet;
import fr.emmuliette.gmmod.characterSheet.SheetCapability;
import fr.emmuliette.gmmod.characterSheet.SheetPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = "gmmod")
public class SyncHandler {
	// PLAYER SERVER SYNC
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(GmMod.MOD_ID, "main")).clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register() {
		int disc = 0;
		HANDLER.registerMessage(disc++, SheetPacket.class, SheetPacket::encode, SheetPacket::decode, SheetPacket.Handler::handle);
	}

	/**
	 * Sends a packet to a specific player.<br>
	 * Must be called server side.
	 */
	public static void sendTo(Object msg, ServerPlayer player) {
		if (!(player instanceof FakePlayer)) {
			if (player.connection == null || player.connection.getConnection() == null)
				return;
			HANDLER.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	/**
	 * Sends a packet to the server.<br>
	 * Must be called client side.
	 */
	public static void sendToServer(Object msg) {
		HANDLER.sendToServer(msg);
	}

	/** Server side. */
	public static void sendToAllPlayers(Object msg, MinecraftServer server) {
		List<ServerPlayer> list = server.getPlayerList().getPlayers();
		for (ServerPlayer e : list) {
			sendTo(msg, e);
		}
	}

	// PLAYER SYNC EVENTS
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		LazyOptional<ISheet> oldCap = event.getOriginal().getCapability(SheetCapability.SHEET_CAPABILITY, null);
		LazyOptional<ISheet> newCap = event.getPlayer().getCapability(SheetCapability.SHEET_CAPABILITY, null);
		ISheet oldPlayer = oldCap.orElse(null);

		if (oldPlayer != null) {
			ISheet newPlayer = newCap.orElse(null);

			if (newPlayer != null) {
				newPlayer.sync(oldPlayer);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
		ServerPlayer player = (ServerPlayer) event.getPlayer();
		player.getCapability(SheetCapability.SHEET_CAPABILITY).ifPresent(c -> c.sync(player));
	}

	@SubscribeEvent
	public static void onRespawnEvent(PlayerRespawnEvent event) {
		event.getPlayer().getCapability(SheetCapability.SHEET_CAPABILITY)
				.ifPresent(c -> c.sync((ServerPlayer) event.getPlayer()));
	}

	@SubscribeEvent
	public static void onPlayerConnect(PlayerLoggedInEvent event) {
		ServerPlayer player = (ServerPlayer) event.getPlayer();
		if (!player.level.isClientSide)
			player.getCapability(SheetCapability.SHEET_CAPABILITY).ifPresent(c -> c.sync(player));
	}
}
