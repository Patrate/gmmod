package fr.emmuliette.gmmod.characterSheet.stats;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
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
import fr.emmuliette.gmmod.exceptions.DummyStatRegisterException;
import fr.emmuliette.gmmod.exceptions.InvalidStatException;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;

public abstract class Stat {
	private static final String VALUE = "value", BONUS = "bonus";
	private static final Map<String, Class<? extends Stat>> registry = new HashMap<String, Class<? extends Stat>>();
	private CharacterSheet sheet;
	private String key;
	private int value, min, max;
	private Map<String, Integer> bonus;

	protected final void setKey(String key) {
		this.key = key.toLowerCase();
	}

	public Stat(String key) {
		this.key = key.toLowerCase();
		this.value = 0;
		this.min = Integer.MIN_VALUE;
		this.max = Integer.MAX_VALUE;
		this.bonus = new HashMap<String, Integer>();
	}

	public static void registerStat(Class<? extends Stat> stat) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, DummyStatRegisterException {
		if(stat.equals(DummyStat.class))
			throw new DummyStatRegisterException();
		Stat instance = stat.getConstructor().newInstance();
		GmMod.logger().info("Registering " + instance.key + " = class " + stat.getSimpleName());
		registry.put(instance.key, stat);
	}

	public static void register() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
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
		} catch(DummyStatRegisterException e) {
			// Can safely ignore, we don't want *others* to register it
		}
	}

	public static Set<String> getRegistry() {
		return registry.keySet();
	}

	public static Collection<Class<? extends Stat>> getAllStats() {
		return registry.values();
	}

	public static String toKey(Class<? extends Stat> statClass) {
		for (String key : registry.keySet()) {
			if (statClass.equals(registry.get(key)))
				return key;
		}
		return "";
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

	public int getValue() {
		return getBaseValue() + getTotalBonus();
	}

	public int getBaseValue() {
		return value;
	}

	public void setBaseValue(int value) {
		if (value == this.value)
			return;
		try {
			int old = getValue();
			this.value = value;
			onChange(old, getValue());
		} catch (StatOutOfBoundsException e) {
			e.printStackTrace();
		} catch (MissingSheetDataException e) {
			GmMod.logger().warn(e.getMessage());
		} catch (InvalidStatException e) {
			// Can silently ignore
		}
	}

	public boolean hasBonus() {
		return !bonus.isEmpty();
	}

	public Map<String, Integer> getBonusMap() {
		return bonus;
	}

	public int setBonus(String key, int value) {
		return bonus.put(key, value);
	}

	public int removeBonus(String key) {
		return bonus.remove(key);
	}

	public void clearBonus() {
		bonus.clear();
	}

	public int getTotalBonus() {
		int retour = 0;
		for (Integer b : bonus.values())
			retour += b;
		return retour;
	}

	public int getMin() {
		return min;
	}

	protected void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	protected void setMax(int max) {
		this.max = max;
	}

	public abstract boolean isValid();

	public void onChange(int oldValue, int newValue)
			throws StatOutOfBoundsException, MissingSheetDataException, InvalidStatException {
		if (!this.isValid())
			throw new InvalidStatException();
		if (this.value > this.getMax()) {
			this.setBaseValue(this.getMax());
			throw new StatOutOfBoundsException(
					"New value " + newValue + " is over " + this.getMax() + " for stat " + this.getKey());
		}
		if (this.value < this.getMin()) {
			this.setBaseValue(this.getMin());
			throw new StatOutOfBoundsException(
					"New value " + newValue + " is under " + this.getMin() + " for stat " + this.getKey());
		}
	}

	public abstract void init() throws StatOutOfBoundsException, MissingSheetDataException, InvalidStatException;

	public final void toNBT(CompoundTag in) {
		in.put(key, toNBT());
	}

	protected CompoundTag toNBT() {
		CompoundTag retour = new CompoundTag();
		retour.putInt(VALUE, getBaseValue());
		if (hasBonus()) {
			CompoundTag bonusTag = new CompoundTag();
			for (String bKey : bonus.keySet()) {
				bonusTag.putInt(bKey, bonus.get(bKey));
			}
			retour.put(BONUS, bonusTag);
		}
		return retour;
	}

	public void fromNBT(CompoundTag nbt) {
		this.setBaseValue(nbt.getInt(VALUE));
		if (nbt.contains(BONUS)) {
			CompoundTag bTag = nbt.getCompound(BONUS);
			for (String bKey : bTag.getAllKeys()) {
				bonus.put(bKey, bTag.getInt(bKey));
			}
		}
	}
}
