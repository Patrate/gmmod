package fr.emmuliette.gmmod.characterSheet;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.jobs.Job;
import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.DuplicateStatException;
import fr.emmuliette.gmmod.exceptions.InvalidStatException;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.MissingStatException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import fr.emmuliette.gmmod.packets.PacketHandler;
import fr.emmuliette.gmmod.packets.SheetPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

public class CharacterSheet implements ICapabilitySerializable<CompoundTag> {
	private LivingEntity owner;
	private Map<String, Job> jobs;
	private Map<Class<? extends Stat>, Stat> stats;

	public CharacterSheet() {
		this(null);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public CharacterSheet(LivingEntity owner) {
		this.owner = owner;
		jobs = new HashMap<String, Job>();
		initStatsInternal();
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void initStats() {
	}

	private void initStatsInternal() {
		stats = new HashMap<Class<? extends Stat>, Stat>();
		try {
			for (Class<? extends Stat> statClass : Stat.getAllStats()) {
				addStat(statClass);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (DuplicateStatException e) {
			e.printStackTrace();
		}
		initStats();
	}

	public void postInit() {
		for (Stat stat : stats.values()) {
			try {
				stat.init();
			} catch (StatOutOfBoundsException | MissingSheetDataException e) {
				GmMod.logger().warn("Error during Stat " + stat.getKey() + " init: " + e.getMessage());
			} catch (InvalidStatException e) {
				// Can silently ignore
			}
		}
		for (Job job : jobs.values()) {
			job.init();
		}
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}

	public void addJob(JobTemplate template) {
		if (hasJob(template.getName()))
			// TODO throw error ? No
			return;
		Job job = template.getJob(this);
		jobs.put(template.getName(), job);
	}

	private void syncJob(JobTemplate template, int level) {
		if (!this.hasJob(template.getName()))
			this.addJob(template);
		this.getJob(template.getName()).setLevel(level);
	}

	public Collection<Job> getJobs() {
		return jobs.values();
	}

	public boolean hasJob(String key) {
		return jobs.containsKey(key);
	}

	public Job getJob(String key) {
		return jobs.get(key);
	}

	public void levelUpJob(String key) {
		if (!hasJob(key))
			// TODO throw error
			return;
		getJob(key).levelUp();
	}

	public Collection<Stat> getStats() {
		Collection<Stat> retour = new ArrayList<Stat>();
		for (Stat s : stats.values()) {
			if (s.isValid())
				retour.add(s);
		}
		return retour;
	}

	public Stat getStat(Class<? extends Stat> stat) throws MissingStatException {
		if (!stats.containsKey(stat)) {
			throw new MissingStatException(this, stat);
		}
		return stats.get(stat);
	}

	protected final void addStat(Class<? extends Stat> statClass)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, DuplicateStatException {
		if (stats.containsKey(statClass)) {
			throw new DuplicateStatException(this, statClass);
		}
		Stat stat = statClass.getConstructor().newInstance();
		stats.put(statClass, stat);
		stat.setSheet(this);
	}

	public void setStat(Class<? extends Stat> statClass, int d) {
		if (!stats.containsKey(statClass)) {
			// TODO throw error
			return;
		}
		stats.get(statClass).setBaseValue(d);
		sync();
	}

	// ==================================== LEVELING ?

	// ==================================== SYNC & DATA

	public void sync(ServerPlayer player) {
		player.getCapability(CharacterSheet.SHEET_CAPABILITY).ifPresent(c -> c.sync());
	}

	public void sync(CharacterSheet other) {
		this.owner = other.owner;
		initStatsInternal();
		for (Class<? extends Stat> statClass : other.stats.keySet()) {
			try {
				Stat otherStat = other.stats.get(statClass);
				this.setStat(statClass, otherStat.getBaseValue());
				if (otherStat.hasBonus()) {
					Map<String, Integer> otherMap = otherStat.getBonusMap();
					for (String bonusKey : otherMap.keySet()) {
						this.getStat(statClass).setBonus(bonusKey, otherMap.get(bonusKey));
					}
				}
			} catch (MissingStatException e) {
				e.printStackTrace();
			}
		}
		for (Job job : other.jobs.values()) {
			this.syncJob(job.template(), job.getLevel());
		}
	}

	public void sync() {
		if (owner != null && getOwner() instanceof ServerPlayer) {
			PacketHandler.sendTo(new SheetPacket(this.serializeNBT()), (ServerPlayer) getOwner());
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag retour = new CompoundTag();

		CompoundTag statsTag = new CompoundTag();
		for (Stat stat : stats.values()) {
			stat.toNBT(statsTag);
		}
		retour.put("stats", statsTag);

		CompoundTag jobsTag = new CompoundTag();
		for (Job job : jobs.values()) {
			job.toNBT(jobsTag);
		}
		retour.put("jobs", jobsTag);

		return retour;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag statsTag = nbt.getCompound("stats");
		for (String key : statsTag.getAllKeys()) {
			try {
				Class<? extends Stat> statClass = Stat.getStat(key);
				if (statClass == null) {
					GmMod.logger().warn("statClass is null for key " + key);
					continue;
				}
				if (!this.stats.containsKey(statClass)) {
					GmMod.logger().warn("ADDING MISSING STAT " + key);
					this.addStat(statClass);
				}
				getStat(statClass).fromNBT(statsTag.getCompound(key));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MissingStatException | DuplicateStatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		CompoundTag jobsTag = nbt.getCompound("jobs");
		for (String key : jobsTag.getAllKeys()) {
			// TODO get the jobs template from key
			JobTemplate template = JobTemplate.getTemplate(key);
			// TODO create the job at X level
			this.addJob(template);
			this.getJob(key).fromNBT(jobsTag.getCompound(key));
		}
	}

	public static final ResourceLocation KEY = new ResourceLocation(GmMod.MOD_ID, "sheet_capability");
	public static final Capability<CharacterSheet> SHEET_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	private final LazyOptional<CharacterSheet> capabilityInstance = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == SHEET_CAPABILITY ? (this.capabilityInstance.cast()) : LazyOptional.empty();
	}

	// EVENT

	@SubscribeEvent
	public void serverTickEvent(ServerTickEvent event) {
		if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && this.owner != null)
			MinecraftForge.EVENT_BUS.post(new SheetTickEvent(event, this));
	}

	@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class SheetCapabilityEvents {
		@SubscribeEvent
		public static void register(RegisterCapabilitiesEvent event) {
			event.register(CharacterSheet.class);
		}

		@SubscribeEvent
		public static void attachCapabilityLivingEntity(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) event.getObject();
				if (!entity.getCapability(SHEET_CAPABILITY).isPresent()) {
					event.addCapability(KEY, new CharacterSheet(entity));
				}
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