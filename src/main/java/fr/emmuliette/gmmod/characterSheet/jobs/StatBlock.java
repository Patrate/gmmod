package fr.emmuliette.gmmod.characterSheet.jobs;

import java.util.HashMap;
import java.util.Map;

import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import net.minecraft.nbt.CompoundTag;

public class StatBlock {
	private Map<Class<? extends Stat>, Integer> statsPerLevel;

	public StatBlock() {
		this.statsPerLevel = new HashMap<Class<? extends Stat>, Integer>();
	}

	public StatBlock clone() {
		StatBlock copy = new StatBlock();
		for (Class<? extends Stat> statClass : this.statsPerLevel.keySet()) {
			copy.statsPerLevel.put(statClass, this.statsPerLevel.get(statClass));
		}
		return copy;
	}

	public int getStatPerLevel(Class<? extends Stat> stat) {
		if (statsPerLevel.containsKey(stat))
			return statsPerLevel.get(stat);
		return 0;
	}

	public void setStatPerLevel(Class<? extends Stat> stat, int bonus) {
		if (bonus == 0)
			statsPerLevel.remove(stat);
		else
			statsPerLevel.put(stat, bonus);
	}

	public Map<Class<? extends Stat>, Integer> getBonus(int level) {
		Map<Class<? extends Stat>, Integer> retour = new HashMap<Class<? extends Stat>, Integer>();
		for (Class<? extends Stat> stat : statsPerLevel.keySet()) {
			retour.put(stat, statsPerLevel.get(stat) * level);
		}
		return retour;
	}

	public CompoundTag toNBT() {
		CompoundTag retour = new CompoundTag();
		for (Class<? extends Stat> statClass : statsPerLevel.keySet()) {
			retour.putInt(Stat.toKey(statClass), statsPerLevel.get(statClass));
		}
		return retour;
	}

	public static StatBlock fromNBT(CompoundTag tag) {
		StatBlock retour = new StatBlock();
		for (String key : tag.getAllKeys()) {
			Class<? extends Stat> statClass = Stat.getStat(key);
			retour.setStatPerLevel(statClass, tag.getInt(key));
		}
		return retour;
	}

}
