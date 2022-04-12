package fr.emmuliette.gmmod.characterSheet.jobs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class JobTemplate {
	private static final Map<String, JobTemplate> registry = new HashMap<String, JobTemplate>();

	private String name;
//	private JobRessource ressource;
	private StatBlock statBlock;

	public JobTemplate(String name, StatBlock statBlock) {
		this.name = name;
//		this.ressource = ressource;
		this.statBlock = statBlock;
	}

	public Job getJob(CharacterSheet owner) {
		return new Job(this, owner);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public JobRessource getRessource() {
//		return ressource;
//	}
//
//	public void setRessource(JobRessource ressource) {
//		this.ressource = ressource;
//	}

	public StatBlock getStatBlock() {
		return statBlock;
	}

	public void setStatBlock(StatBlock statBlock) {
		this.statBlock = statBlock;
	}

	private CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("name", name);
		tag.put("stats", statBlock.toNBT());
		return tag;
	}

	private static JobTemplate fromNBT(CompoundTag tag) {

		String name = tag.getString("name");
		StatBlock stats = StatBlock.fromNBT(tag.getCompound("stats"));
		return new JobTemplate(name, stats);
	}

	public static JobTemplate getTemplate(String key) {
		return registry.get(key);
	}

	public static Set<String> getJobsList() {
		return registry.keySet();
	}

	@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class JobTemplateLoader {
		private static final String FILE_PATH = "jobtemplates.nbt";

		@SubscribeEvent
		public static void loadJobsTemplates(WorldEvent.Load event) {
			File inFile = new File(FILE_PATH);
			if (!inFile.exists())
				return;
			CompoundTag jobTemplates = null;
			try {
				jobTemplates = NbtIo.readCompressed(inFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (jobTemplates == null)
				// TODO throw error
				return;
			for (String key : jobTemplates.getAllKeys()) {
				registry.put(key, fromNBT(jobTemplates.getCompound(key)));
			}
		}

		@SubscribeEvent
		public static void saveJobsTemplates(WorldEvent.Save event) {
			CompoundTag jobTemplates = new CompoundTag();
			for (String key : registry.keySet()) {
				jobTemplates.put(key, registry.get(key).toNBT());
			}
			File outFile = new File(FILE_PATH);
			try {
				NbtIo.writeCompressed(jobTemplates, outFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// PLAYER SYNC EVENTS
		@SubscribeEvent
		public static void onPlayerClone(PlayerEvent.Clone event) {
			LazyOptional<CharacterSheet> oldCap = event.getOriginal().getCapability(CharacterSheet.SHEET_CAPABILITY,
					null);
			LazyOptional<CharacterSheet> newCap = event.getPlayer().getCapability(CharacterSheet.SHEET_CAPABILITY,
					null);
			CharacterSheet oldPlayer = oldCap.orElse(null);

			if (oldPlayer != null) {
				CharacterSheet newPlayer = newCap.orElse(new CharacterSheet(event.getPlayer()));

				if (newPlayer != null) {
					newPlayer.sync(oldPlayer);
				}
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
			ServerPlayer player = (ServerPlayer) event.getPlayer();
			player.getCapability(CharacterSheet.SHEET_CAPABILITY).ifPresent(c -> c.sync(player));
		}

		@SubscribeEvent
		public static void onRespawnEvent(PlayerRespawnEvent event) {
			event.getPlayer().getCapability(CharacterSheet.SHEET_CAPABILITY)
					.ifPresent(c -> c.sync((ServerPlayer) event.getPlayer()));
		}

		@SubscribeEvent
		public static void onPlayerConnect(PlayerLoggedInEvent event) {
			ServerPlayer player = (ServerPlayer) event.getPlayer();
			if (!player.level.isClientSide) {
				player.getCapability(CharacterSheet.SHEET_CAPABILITY).ifPresent(c -> c.sync(player));
				player.getCapability(CharacterSheet.SHEET_CAPABILITY).ifPresent(c -> c.postInit());
			}
		}
	}
}
