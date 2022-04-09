package fr.emmuliette.gmmod.characterSheet.stats;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
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
import fr.emmuliette.gmmod.characterSheet.stats.gmmod.HealthRegen;
import fr.emmuliette.gmmod.characterSheet.stats.gmmod.StrongStomach;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;

public abstract class Stat {
	private static final String VALUE = "value";
	private static final Map<String, Class<? extends Stat>> registry = new HashMap<String, Class<? extends Stat>>();
	private CharacterSheet sheet;
	private double value;
	private String key;
	private double min, max;

	protected final void setKey(String key) {
		this.key = key.toLowerCase();
	}

	public Stat(String key) {
		this.key = key.toLowerCase();
		this.value = 0;
		this.min = Double.MIN_VALUE;
		this.max = Double.MAX_VALUE;
	}

	public static void registerStat(Class<? extends Stat> stat) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Stat instance = stat.getConstructor().newInstance();
		GmMod.logger().info("Registering " + instance.key + " = class " + stat.getSimpleName());
		registry.put(instance.key, stat);
	}

	public static void register() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		registerStat(Armor.class);
		registerStat(ArmorToughness.class);
		registerStat(AttackDamage.class);
		registerStat(AttackKnockback.class);
		registerStat(AttackSpeed.class);
		registerStat(FlyingSpeed.class);
		registerStat(KnockbackResistance.class);
		registerStat(Luck.class);
		registerStat(MaxHealth.class);
		registerStat(MovementSpeed.class);
		registerStat(HealthRegen.class);
		registerStat(StrongStomach.class);
	}

	public static Set<String> getRegistry() {
		return registry.keySet();
	}

	public static Class<? extends Stat> getStat(String key) {
		return registry.get(key.toLowerCase());
	}

	public final CharacterSheet getSheet() {
		return this.sheet;
	}

	public final void setSheet(CharacterSheet sheet) {
		this.sheet = sheet;
	}

	public LivingEntity getOwner() {
		return this.sheet == null ? null : this.sheet.getOwner();
	}

	public String getKey() {
		return key;
	}

	public TranslatableComponent getName() {
		return new TranslatableComponent(key);
	}

	public TranslatableComponent getTooltip() {
		return new TranslatableComponent(key + ".tooltip");
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		if (value == this.value)
			return;
		try {
			double old = this.value;
			this.value = value;
			onChange(old, this.value);
		} catch (StatOutOfBoundsException e) {
			e.printStackTrace();
		} catch (MissingSheetDataException e) {
			GmMod.logger().warn(e.getMessage());
		}
	}

	public double getMin() {
		return min;
	}

	protected void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	protected void setMax(double max) {
		this.max = max;
	}

	public void onChange(double oldValue, double newValue) throws StatOutOfBoundsException, MissingSheetDataException {
		if (newValue > this.getMax()) {
			this.setValue(this.getMax());
			throw new StatOutOfBoundsException(
					"New value " + newValue + " is over " + this.getMax() + " for stat " + this.getKey());
		}
		if (newValue < this.getMin()) {
			this.setValue(this.getMin());
			throw new StatOutOfBoundsException(
					"New value " + newValue + " is under " + this.getMin() + " for stat " + this.getKey());
		}

	}

	public abstract void init() throws StatOutOfBoundsException, MissingSheetDataException;

	public final void toNBT(CompoundTag in) {
		in.put(key, toNBT());
	}

	protected CompoundTag toNBT() {
		CompoundTag retour = new CompoundTag();
		retour.putDouble(VALUE, getValue());
		return retour;
	}

	public void fromNBT(CompoundTag nbt) {
		this.setValue(nbt.getFloat(VALUE));
	}
}
