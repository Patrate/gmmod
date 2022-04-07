package fr.emmuliette.gmmod.characterSheet.stats;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.SheetTickEvent;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HealthRegen extends Stat {
	private static final double STEP_VAL = 20., DEFAULT_VAL = 200. + STEP_VAL;
	private static final String TICK = "tick";
	private int tick;

	public HealthRegen() {
		super(GmMod.MOD_ID + ".HealthRegen");
		this.tick = (int) (DEFAULT_VAL - STEP_VAL * this.getValue());
		this.setMin(0.);
		this.setMax(10.);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onChange(double oldValue, double newValue) throws StatOutOfBoundsException {
		super.onChange(oldValue, newValue);
		if (newValue == 0) {
			tick = (int) DEFAULT_VAL;
		} else if (oldValue == 0) {
			tick = (int) (DEFAULT_VAL - STEP_VAL * this.getValue());
		} else {
			tick = Math.max(1, (int) (tick + STEP_VAL * (oldValue - newValue)));
		}
	}

	@SubscribeEvent
	public void regenTickEvent(SheetTickEvent event) {
		if (this.getValue() <= 0)
			return;
		if (!event.sheet.equals(this.getSheet()))
			return;
		if (tick <= 0) {
			event.owner.heal(1f);
			tick = (int) (DEFAULT_VAL - STEP_VAL * this.getValue());
			return;
		}
		tick -= 1;
	}

	@Override
	protected CompoundTag toNBT() {
		CompoundTag retour = super.toNBT();
		retour.putInt(TICK, tick);
		return retour;
	}

	@Override
	public void fromNBT(CompoundTag nbt) {
		super.fromNBT(nbt);
		this.tick = nbt.getInt(TICK);
	}
}
