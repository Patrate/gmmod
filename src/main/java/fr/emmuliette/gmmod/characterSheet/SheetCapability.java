package fr.emmuliette.gmmod.characterSheet;

import fr.emmuliette.gmmod.GmMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SheetCapability implements ICapabilitySerializable<CompoundTag> {
	public static final String SHEET_CAPABILITY_NAME = "sheet_capability";

	public static final Capability<ISheet> SHEET_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public SheetCapability() {
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == SHEET_CAPABILITY ? LazyOptional.of(() -> new SheetCapability()).cast() : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		if (!SHEET_CAPABILITY.isRegistered())
			return new CompoundTag();
		return this.getCapability(SHEET_CAPABILITY).orElse(null).toNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (!SHEET_CAPABILITY.isRegistered())
			return;
		this.getCapability(SHEET_CAPABILITY).orElse(null).fromNBT(nbt);
	}

	@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class SheetCapabilityEvents {
		@SubscribeEvent
		public static void register(RegisterCapabilitiesEvent event) {
			event.register(SheetCapability.class);
		}

		@SubscribeEvent
		public static void onConnect(EntityJoinWorldEvent event) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				player.getCapability(SHEET_CAPABILITY).ifPresent(c -> c.addCount());
				player.getCapability(SHEET_CAPABILITY)
						.ifPresent(c -> System.out.println("LOGGED " + c.getCount() + " TIMES !"));
			}
		}

		@SubscribeEvent
		public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player) {
				System.out.println("Attaching capability !");
				event.addCapability(new ResourceLocation(GmMod.MOD_ID, SheetCapability.SHEET_CAPABILITY_NAME),
						new SheetCapability());
				event.getObject().getCapability(SHEET_CAPABILITY).ifPresent(c -> c.setOwner(event.getObject()));
			}
		}
	}
}