package fr.emmuliette.gmmod.characterSheet.stats;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public abstract class Stat {
	private static final Map<String, Class<? extends Stat>> registry = new HashMap<String, Class<? extends Stat>>();
	private CharacterSheet sheet;
	private double value;
	private String key;

	protected final void setKey(String key) {
		this.key = key;
	}
	
	public Stat(String key) {
		this.key = key;
		this.value = 0;
	}

	public static void registerStat(Class<? extends Stat> stat) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Stat instance = stat.getConstructor().newInstance();
		System.out.println("Registering " + instance.key + " = class " + stat.getSimpleName());
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
	}

	public static Set<String> getRegistry() {
		return registry.keySet();
	}

	public static Class<? extends Stat> getStat(String key) {
		return registry.get(key);
	}

	public final void setSheet(CharacterSheet sheet) {
		this.sheet = sheet;
	}

	public Entity getOwner() {
		return this.sheet == null ? null : this.sheet.getOwner();
	}

	public String getLocation() {
		return key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		if (value == this.value)
			return;
		double old = this.value;
		this.value = value;
		onChange(old, this.value);
	}

	public abstract void onChange(double oldValue, double newValue);

	public void mergeNBT(CompoundTag in) {
		in.putDouble(getLocation().toString(), getValue());
	}

	public CompoundTag toNBT() {
		CompoundTag retour = new CompoundTag();
		retour.putDouble(getLocation().toString(), getValue());
		return retour;
	}

	public void sync(CompoundTag nbt) {
		this.setValue(nbt.getFloat(getLocation().toString()));
	}

//	public static Stat fromName(String name, Float value) {
//		if (!registry.containsKey(name)) {
//			// TODO throw error
//			System.out.println("STAT " + name + " not found !");
//			return null;
//		}
//		try {
//			return registry.get(name).getDeclaredConstructor(Float.class).newInstance(value);
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			// TODO throw error ?
//			e.printStackTrace();
//		}
//		return null;
//	}
}
