package fr.emmuliette.gmmod.characterSheet.jobs;

import java.util.Map;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.MissingStatException;
import net.minecraft.nbt.CompoundTag;

public class Job {
	private static final String JOB_PREFIX = "job.";
	private CharacterSheet owner;
	private JobTemplate template;
	private int level;

	public Job(JobTemplate template, CharacterSheet owner) {
		this.template = template;
		this.level = 0;
		this.owner = owner;
	}

	public void init() {
		onChange();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return template().getName();
	}

	public void levelUp() {
		level += 1;
		onChange();
	}

	public void clear() {
		for (Stat stat : owner.getStats()) {
			stat.removeBonus(JOB_PREFIX + getName());
		}
	}

	public void onChange() {
		if (level == 0)
			clear();
		Map<Class<? extends Stat>, Integer> bonuses = template().getStatBlock().getBonus(level);
		for (Class<? extends Stat> stat : bonuses.keySet()) {
			try {
				this.owner.getStat(stat).setBonus(JOB_PREFIX + getName(), bonuses.get(stat));
			} catch (MissingStatException e) {
				e.printStackTrace();
			}
		}
	}

	public JobTemplate template() {
		return template;
	}

	public void toNBT(CompoundTag tag) {
		CompoundTag jobTag = new CompoundTag();
		jobTag.putInt("level", level);
		tag.put(this.getName(), jobTag);
	}

	public void fromNBT(CompoundTag tag) {
		this.level = tag.getInt("level");
	}
}
