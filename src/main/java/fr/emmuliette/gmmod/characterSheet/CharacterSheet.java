package fr.emmuliette.gmmod.characterSheet;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.stats.HealthRegen;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.Armor;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.ArmorToughness;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.AttackDamage;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.AttackKnockback;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.AttackSpeed;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.FlyingSpeed;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.KnockbackResistance;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.Luck;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.MaxHealth;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.MovementSpeed;
import fr.emmuliette.gmmod.jobs.Job;
import fr.emmuliette.gmmod.packets.PacketHandler;
import fr.emmuliette.gmmod.packets.SheetPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CharacterSheet implements ICapabilitySerializable<CompoundTag> {
	private Entity owner;
	private Map<Job, Integer> jobs;
	private Map<Class<? extends Stat>, Stat> stats;

	public CharacterSheet(Entity owner) {
		this.owner = owner;
		jobs = new HashMap<Job, Integer>();
		initStatsInternal();
	}

	protected void initStats() {
	}

	private void initStatsInternal() {
		stats = new HashMap<Class<? extends Stat>, Stat>();
		try {
			addStat(Armor.class);
			addStat(ArmorToughness.class);
			addStat(AttackDamage.class);
			addStat(AttackKnockback.class);
			addStat(AttackSpeed.class);
			addStat(FlyingSpeed.class);
			addStat(KnockbackResistance.class);
			addStat(Luck.class);
			addStat(MaxHealth.class);
			addStat(MovementSpeed.class);

			addStat(HealthRegen.class);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		initStats();
	}

	public CharacterSheet() {
		this(null);
	}

	public Entity getOwner() {
		return owner;
	}

	public void setOwner(Entity owner) {
		this.owner = owner;
	}

	public Map<Job, Integer> getJobs() {
		return jobs;
	}

	public void setJobs(Map<Job, Integer> jobs) {
		this.jobs = jobs;
	}

	public Collection<Stat> getStats() {
		return stats.values();
	}

	public Stat getStat(Class<? extends Stat> stat) {
		if (!stats.containsKey(stat)) {
			// TODO throw error
			return null;
		}
		return stats.get(stat);
	}

	protected final void addStat(Class<? extends Stat> statClass) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (stats.containsKey(statClass)) {
			// TODO throw error
			return;
		}
		Stat stat;
		stat = statClass.getConstructor().newInstance();
		stats.put(statClass, stat);
		stat.setSheet(this);
	}

	public void setStat(Class<? extends Stat> statClass, double d) {
		if (!stats.containsKey(statClass)) {
			// TODO throw error
			return;
		}
		stats.get(statClass).setValue(d);
		sync();
	}

	// ====================================

	public void sync(ServerPlayer player) {
		player.getCapability(CharacterSheet.SHEET_CAPABILITY).ifPresent(c -> c.sync());
	}

	public void sync(CharacterSheet other) {
		this.owner = other.owner;
		initStatsInternal();
		for (Class<? extends Stat> statClass : other.stats.keySet()) {
			this.setStat(statClass, other.stats.get(statClass).getValue());
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
			stat.mergeNBT(statsTag);
		}
		retour.put("stats", statsTag);
		return retour;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag statsTag = nbt.getCompound("stats");
		for (String key : statsTag.getAllKeys()) {
			setStat(Stat.getStat(key), statsTag.getFloat(key));
		}
	}

	public static final ResourceLocation KEY = new ResourceLocation(GmMod.MOD_ID, "sheet_capability");
	public static final Capability<CharacterSheet> SHEET_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	private final LazyOptional<CharacterSheet> capabilityInstance = LazyOptional.of(() -> this);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == SHEET_CAPABILITY ? this.capabilityInstance.cast() : LazyOptional.empty();
	}

	@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class SheetCapabilityEvents {
		@SubscribeEvent
		public static void register(RegisterCapabilitiesEvent event) {
			event.register(CharacterSheet.class);
		}

		@SubscribeEvent
		public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player) {
				Player player = (Player) event.getObject();
				if (!player.getCapability(SHEET_CAPABILITY).isPresent()) {
					event.addCapability(KEY, new CharacterSheet(event.getObject()));
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
			}
		}
	}
}